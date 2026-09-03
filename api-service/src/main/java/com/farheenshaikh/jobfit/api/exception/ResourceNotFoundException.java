package com.farheenshaikh.jobfit.api.exception;

/** Thrown when a job, resume, or analysis result looked up by id doesn't exist. Maps to 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
