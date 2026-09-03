package com.farheenshaikh.jobfit.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Request body for {@code POST /api/jobs}. */
public class CreateJobRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotEmpty(message = "requiredSkills must contain at least one skill")
    private List<String> requiredSkills;

    protected CreateJobRequest() {
        // Jackson
    }

    public CreateJobRequest(String title, String description, List<String> requiredSkills) {
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }
}
