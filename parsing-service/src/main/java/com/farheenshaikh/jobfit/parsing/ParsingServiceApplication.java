package com.farheenshaikh.jobfit.parsing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hosts {@link com.farheenshaikh.jobfit.parsing.event.ResumeUploadedEventHandler}, an
 * {@code OutboxPoller} subclass whose {@code @Scheduled} polling loop needs
 * {@code @EnableScheduling} turned on somewhere -- here, since this is the only scheduled
 * component in this service. Scan/entity-scan/repository-scan are widened to
 * {@code com.farheenshaikh.jobfit} for the same reason as api-service: {@code jobfit-common}'s
 * entities and repositories live in a sibling package.
 */
@SpringBootApplication(scanBasePackages = "com.farheenshaikh.jobfit")
@EntityScan("com.farheenshaikh.jobfit.common.model")
@EnableJpaRepositories("com.farheenshaikh.jobfit.common.repository")
@EnableScheduling
public class ParsingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParsingServiceApplication.class, args);
    }
}
