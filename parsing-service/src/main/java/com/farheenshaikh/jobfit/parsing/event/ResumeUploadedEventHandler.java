package com.farheenshaikh.jobfit.parsing.event;

import com.farheenshaikh.jobfit.common.event.EventPublisher;
import com.farheenshaikh.jobfit.common.event.OutboxPoller;
import com.farheenshaikh.jobfit.common.event.payload.ResumeParsedEvent;
import com.farheenshaikh.jobfit.common.event.payload.ResumeUploadedEvent;
import com.farheenshaikh.jobfit.common.model.ParsedResume;
import com.farheenshaikh.jobfit.common.model.Resume;
import com.farheenshaikh.jobfit.common.model.ResumeStatus;
import com.farheenshaikh.jobfit.common.repository.ParsedResumeRepository;
import com.farheenshaikh.jobfit.common.repository.ResumeRepository;
import com.farheenshaikh.jobfit.common.storage.BlobStorage;
import com.farheenshaikh.jobfit.parsing.skill.ExtractedFields;
import com.farheenshaikh.jobfit.parsing.skill.ResumeFieldExtractor;
import com.farheenshaikh.jobfit.parsing.text.TextExtractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code "resume.uploaded"}: reads the file back out of {@link BlobStorage}, runs
 * the two-stage extraction (text, then fields), saves a {@link ParsedResume}, and publishes
 * {@code "resume.parsed"} for analysis-service. {@link OutboxPoller#handle} already runs
 * inside a transaction (see {@code OutboxPoller.pollLoop}), so every write here either all
 * lands together or none does.
 */
@Component
public class ResumeUploadedEventHandler extends OutboxPoller {

    private final ObjectMapper objectMapper;
    private final ResumeRepository resumeRepository;
    private final ParsedResumeRepository parsedResumeRepository;
    private final BlobStorage blobStorage;
    private final TextExtractionService textExtractionService;
    private final ResumeFieldExtractor fieldExtractor;
    private final EventPublisher eventPublisher;

    public ResumeUploadedEventHandler(ObjectMapper objectMapper, ResumeRepository resumeRepository,
                                       ParsedResumeRepository parsedResumeRepository, BlobStorage blobStorage,
                                       TextExtractionService textExtractionService,
                                       ResumeFieldExtractor fieldExtractor, EventPublisher eventPublisher) {
        this.objectMapper = objectMapper;
        this.resumeRepository = resumeRepository;
        this.parsedResumeRepository = parsedResumeRepository;
        this.blobStorage = blobStorage;
        this.textExtractionService = textExtractionService;
        this.fieldExtractor = fieldExtractor;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected String topic() {
        return "resume.uploaded";
    }

    @Override
    protected void handle(String payloadJson) throws Exception {
        ResumeUploadedEvent event = objectMapper.readValue(payloadJson, ResumeUploadedEvent.class);

        Resume resume = resumeRepository.findById(event.getResumeId())
                .orElseThrow(() -> new IllegalStateException("resume " + event.getResumeId() + " not found"));

        resume.markStatus(ResumeStatus.PARSING);
        resumeRepository.save(resume);

        try {
            byte[] data = blobStorage.read(event.getStorageKey());
            String text = textExtractionService.extract(data, event.getContentType(), event.getOriginalFilename());
            ExtractedFields fields = fieldExtractor.extract(text);

            parsedResumeRepository.save(new ParsedResume(resume.getId(), text, fields.skills(),
                    fields.yearsExperience(), fields.education()));

            resume.markStatus(ResumeStatus.PARSED);
            resumeRepository.save(resume);

            eventPublisher.publish("resume.parsed", new ResumeParsedEvent(resume.getId(), resume.getJobId()));
        } catch (Exception e) {
            resume.markFailed("parsing failed: " + e.getMessage());
            resumeRepository.save(resume);
            throw e; // also marks the outbox event FAILED, for observability
        }
    }
}
