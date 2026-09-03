package com.farheenshaikh.jobfit.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hosts {@link com.farheenshaikh.jobfit.analysis.event.ResumeParsedEventHandler}'s
 * {@code @Scheduled} polling loop -- same scan-widening + {@code @EnableScheduling} pattern
 * as parsing-service, for the same reasons.
 */
@SpringBootApplication(scanBasePackages = "com.farheenshaikh.jobfit")
@EntityScan("com.farheenshaikh.jobfit.common.model")
@EnableJpaRepositories("com.farheenshaikh.jobfit.common.repository")
@EnableScheduling
public class AnalysisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisServiceApplication.class, args);
    }
}
