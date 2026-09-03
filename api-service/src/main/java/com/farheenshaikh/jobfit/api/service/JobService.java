package com.farheenshaikh.jobfit.api.service;

import com.farheenshaikh.jobfit.api.dto.CreateJobRequest;
import com.farheenshaikh.jobfit.api.exception.ResourceNotFoundException;
import com.farheenshaikh.jobfit.common.model.Job;
import com.farheenshaikh.jobfit.common.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD for job postings. No async workflow here -- that's all on the resume side. */
@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job createJob(CreateJobRequest request) {
        Job job = new Job(request.getTitle(), request.getDescription(), request.getRequiredSkills());
        return jobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public List<Job> listJobs() {
        return jobRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("no job with id " + id));
    }
}
