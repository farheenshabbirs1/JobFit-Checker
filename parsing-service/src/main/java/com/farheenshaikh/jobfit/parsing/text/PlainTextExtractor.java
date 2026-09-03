package com.farheenshaikh.jobfit.parsing.text;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/** Handles .txt uploads, and doubles as the fallback nothing else claims. */
@Component
@Order(Order.LOWEST_PRECEDENCE) // fallback: only picked when no more specific extractor supports the file
public class PlainTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String contentType, String filename) {
        if (contentType != null && contentType.startsWith("text/")) {
            return true;
        }
        return filename != null && filename.toLowerCase().endsWith(".txt");
    }

    @Override
    public String extract(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
