package com.farheenshaikh.jobfit.api.controller;

import com.farheenshaikh.jobfit.api.dto.AnalysisResponse;
import com.farheenshaikh.jobfit.api.dto.ResumeResponse;
import com.farheenshaikh.jobfit.api.service.ResumeService;
import com.farheenshaikh.jobfit.api.service.ResumeUploadService;
import com.farheenshaikh.jobfit.common.model.Resume;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

/**
 * Resume upload and status/result lookups. Upload only kicks off the async pipeline
 * (see {@code ResumeUploadService}) -- it never parses or scores anything itself, so it
 * returns as soon as the file and outbox event are durably written, well under the time a
 * synchronous "upload + parse + score" call would take.
 */
@RestController
public class ResumeController {

    private final ResumeUploadService resumeUploadService;
    private final ResumeService resumeService;

    public ResumeController(ResumeUploadService resumeUploadService, ResumeService resumeService) {
        this.resumeUploadService = resumeUploadService;
        this.resumeService = resumeService;
    }

    @PostMapping("/api/jobs/{jobId}/resumes")
    public ResponseEntity<ResumeResponse> uploadResume(@PathVariable Long jobId,
                                                         @RequestParam String candidateName,
                                                         @RequestParam("file") MultipartFile file) {
        Resume resume = resumeUploadService.upload(jobId, candidateName, file);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .location(URI.create("/api/resumes/" + resume.getId()))
                .body(ResumeResponse.from(resume));
    }

    @GetMapping("/api/jobs/{jobId}/resumes")
    public List<ResumeResponse> listResumesForJob(@PathVariable Long jobId) {
        return resumeService.listByJob(jobId).stream().map(ResumeResponse::from).toList();
    }

    @GetMapping("/api/resumes/{id}")
    public ResumeResponse getResume(@PathVariable Long id) {
        return ResumeResponse.from(resumeService.getResume(id));
    }

    @GetMapping("/api/resumes/{id}/analysis")
    public AnalysisResponse getAnalysis(@PathVariable Long id) {
        return AnalysisResponse.from(resumeService.getAnalysis(id));
    }
}
