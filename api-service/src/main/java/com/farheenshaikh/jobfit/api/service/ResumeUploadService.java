package com.farheenshaikh.jobfit.api.service;

import com.farheenshaikh.jobfit.api.exception.ResourceNotFoundException;
import com.farheenshaikh.jobfit.common.event.EventPublisher;
import com.farheenshaikh.jobfit.common.event.payload.ResumeUploadedEvent;
import com.farheenshaikh.jobfit.common.model.Job;
import com.farheenshaikh.jobfit.common.model.Resume;
import com.farheenshaikh.jobfit.common.repository.JobRepository;
import com.farheenshaikh.jobfit.common.repository.ResumeRepository;
import com.farheenshaikh.jobfit.common.storage.BlobStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Handles a resume upload end to end: writes the file bytes to {@link BlobStorage}, saves the
 * {@code Resume} row, and publishes {@code "resume.uploaded"} -- all inside one transaction,
 * so a crash between "file is stored" and "event is published" can't happen. (The file write
 * itself isn't covered by the DB transaction, but it happens first and the row/event are only
 * committed once it has already succeeded, so a failed write just aborts the upload cleanly
 * with nothing left half-done in the database.)
 */
@Service
public class ResumeUploadService {

    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final BlobStorage blobStorage;
    private final EventPublisher eventPublisher;

    public ResumeUploadService(JobRepository jobRepository, ResumeRepository resumeRepository,
                                BlobStorage blobStorage, EventPublisher eventPublisher) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.blobStorage = blobStorage;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Resume upload(Long jobId, String candidateName, MultipartFile file) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("no job with id " + jobId));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("resume file is required");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        String storageKey;
        try {
            storageKey = blobStorage.store(originalFilename, file.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("failed to store resume file", e);
        }

        Resume resume = new Resume(job.getId(), candidateName, originalFilename, contentType, storageKey);
        resume = resumeRepository.save(resume);

        eventPublisher.publish("resume.uploaded", new ResumeUploadedEvent(
                resume.getId(), job.getId(), storageKey, originalFilename, contentType));

        return resume;
    }
}
