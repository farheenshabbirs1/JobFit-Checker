package com.farheenshaikh.jobfit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The only service the frontend talks to directly. Component/entity/repository scanning is
 * widened to {@code com.farheenshaikh.jobfit} so the shared {@code jobfit-common} module's
 * entities, repositories, and beans (the outbox publisher, blob storage) are picked up even
 * though they live in a sibling package, not a sub-package of this class.
 */
@SpringBootApplication(scanBasePackages = "com.farheenshaikh.jobfit")
@EntityScan("com.farheenshaikh.jobfit.common.model")
@EnableJpaRepositories("com.farheenshaikh.jobfit.common.repository")
public class ApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiServiceApplication.class, args);
    }
}
