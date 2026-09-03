package com.farheenshaikh.jobfit.parsing.text;

import java.io.IOException;

/** Stage 1 of parsing: turn raw file bytes into plain text. */
public interface TextExtractor {

    /** Whether this extractor knows how to handle a file with this content type/filename. */
    boolean supports(String contentType, String filename);

    String extract(byte[] data) throws IOException;
}
