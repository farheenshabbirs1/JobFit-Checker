package com.farheenshaikh.jobfit.analysis.event;

import com.farheenshaikh.jobfit.analysis.scoring.QualificationScorer;
import com.farheenshaikh.jobfit.analysis.scoring.ScoreResult;
import com.farheenshaikh.jobfit.analysis.suggestion.SuggestionGenerator;
import com.farheenshaikh.jobfit.common.event.OutboxPoller;
import com.farheenshaikh.jobfit.common.event.payload.ResumeParsedEvent;
import com.farheenshaikh.jobfit.common.model.AnalysisResult;
import com.farheenshaikh.jobfit.common.model.Job;
import com.farheenshaikh.jobfit.common.model.ParsedResume;
import com.farheenshaikh.jobfit.common.model.Resume;
import com.farheenshaikh.jobfit.common.model.ResumeStatus;
import com.farheenshaikh.jobfit.common.repository.AnalysisResultRepository;
import com.farheenshaikh.jobfit.common.repository.JobRepository;
import com.farheenshaikh.jobfit.common.repository.ParsedResumeRepository;
import com.farheenshaikh.jobfit.common.repository.ResumeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code "resume.parsed"}: loads the job posting and the parsed resume, scores the
 * candidate's skill coverage, generates suggestions for the gap, writes the final
 * {@link AnalysisResult}, and moves the resume to {@link ResumeStatus#DONE}. This is the last
 * stage of the pipeline -- from here the frontend's poll of {@code GET /api/resumes/{id}}
 * sees a terminal status and can fetch {@code GET /api/resumes/{id}/analysis}.
 */
@Component
public class ResumeParsedEventHandler extends OutboxPoller {

    private final ObjectMapper objectMapper;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final ParsedResumeRepository parsedResumeRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final QualificationScorer qualificationScorer;
    private final SuggestionGenerator suggestionGenerator;

    public ResumeParsedEventHandler(ObjectMapper objectMapper, JobRepository jobRepository,
                                     ResumeRepository resumeRepository,
                                     ParsedResumeRepository parsedResumeRepository,
                                     AnalysisResultRepository analysisResultRepository,
                                     QualificationScorer qualificationScorer,
                                     SuggestionGenerator suggestionGenerator) {
        this.objectMapper = objectMapper;
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.parsedResumeRepository = parsedResumeRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.qualificationScorer = qualificationScorer;
        this.suggestionGenerator = suggestionGenerator;
    }

    @Override
    protected String topic() {
        return "resume.parsed";
    }

    @Override
    protected void handle(String payloadJson) throws Exception {
        ResumeParsedEvent event = objectMapper.readValue(payloadJson, ResumeParsedEvent.class);

        Resume resume = resumeRepository.findById(event.getResumeId())
                .orElseThrow(() -> new IllegalStateException("resume " + event.getResumeId() + " not found"));

        resume.markStatus(ResumeStatus.ANALYZING);
        resumeRepository.save(resume);

        try {
            Job job = jobRepository.findById(event.getJobId())
                    .orElseThrow(() -> new IllegalStateException("job " + event.getJobId() + " not found"));
            ParsedResume parsedResume = parsedResumeRepository.findById(event.getResumeId())
                    .orElseThrow(() -> new IllegalStateException(
                            "parsed resume " + event.getResumeId() + " not found"));

            ScoreResult scoreResult = qualificationScorer.score(job.getRequiredSkills(), parsedResume.getSkills());
            String suggestions = suggestionGenerator.generate(job, parsedResume, scoreResult);

            analysisResultRepository.save(new AnalysisResult(job.getId(), resume.getId(),
                    scoreResult.qualificationScore(), scoreResult.matchedSkills(), scoreResult.missingSkills(),
                    suggestions));

            resume.markStatus(ResumeStatus.DONE);
            resumeRepository.save(resume);
        } catch (Exception e) {
            resume.markFailed("analysis failed: " + e.getMessage());
            resumeRepository.save(resume);
            throw e; // also marks the outbox event FAILED, for observability
        }
    }
}
