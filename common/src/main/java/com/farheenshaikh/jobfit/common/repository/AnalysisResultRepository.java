package com.farheenshaikh.jobfit.common.repository;

import com.farheenshaikh.jobfit.common.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    Optional<AnalysisResult> findByResumeId(Long resumeId);
}
