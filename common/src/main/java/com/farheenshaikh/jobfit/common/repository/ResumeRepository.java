package com.farheenshaikh.jobfit.common.repository;

import com.farheenshaikh.jobfit.common.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByJobId(Long jobId);
}
