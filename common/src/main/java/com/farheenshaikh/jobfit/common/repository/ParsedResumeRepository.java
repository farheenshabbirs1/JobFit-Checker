package com.farheenshaikh.jobfit.common.repository;

import com.farheenshaikh.jobfit.common.model.ParsedResume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsedResumeRepository extends JpaRepository<ParsedResume, Long> {
}
