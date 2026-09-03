package com.farheenshaikh.jobfit.api.service;

import com.farheenshaikh.jobfit.api.exception.ResourceNotFoundException;
import com.farheenshaikh.jobfit.common.model.AnalysisResult;
import com.farheenshaikh.jobfit.common.model.Resume;
import com.farheenshaikh.jobfit.common.repository.AnalysisResultRepository;
import com.farheenshaikh.jobfit.common.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read side of the resume workflow: status lookups and the final analysis, once
 * analysis-service has produced one. Upload (the write side) is {@link ResumeUploadService}.
 */
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public ResumeService(ResumeRepository resumeRepository, AnalysisResultRepository analysisResultRepository) {
        this.resumeRepository = resumeRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    @Transactional(readOnly = true)
    public Resume getResume(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("no resume with id " + id));
    }

    @Transactional(readOnly = true)
    public List<Resume> listByJob(Long jobId) {
        return resumeRepository.findByJobId(jobId);
    }

    @Transactional(readOnly = true)
    public AnalysisResult getAnalysis(Long resumeId) {
        return analysisResultRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "no analysis yet for resume " + resumeId + " -- check its status first"));
    }
}
