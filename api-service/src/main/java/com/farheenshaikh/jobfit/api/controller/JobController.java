package com.farheenshaikh.jobfit.api.controller;

import com.farheenshaikh.jobfit.api.dto.CreateJobRequest;
import com.farheenshaikh.jobfit.api.dto.JobResponse;
import com.farheenshaikh.jobfit.api.service.JobService;
import com.farheenshaikh.jobfit.common.model.Job;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Job posting CRUD -- what candidates' resumes get scored against. */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        Job job = jobService.createJob(request);
        return ResponseEntity.created(URI.create("/api/jobs/" + job.getId())).body(JobResponse.from(job));
    }

    @GetMapping
    public List<JobResponse> listJobs() {
        return jobService.listJobs().stream().map(JobResponse::from).toList();
    }

    @GetMapping("/{id}")
    public JobResponse getJob(@PathVariable Long id) {
        return JobResponse.from(jobService.getJob(id));
    }
}
