package com.farheenshaikh.jobfit.common.repository;

import com.farheenshaikh.jobfit.common.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
