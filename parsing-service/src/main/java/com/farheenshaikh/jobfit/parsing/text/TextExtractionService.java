package com.farheenshaikh.jobfit.parsing.text;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Picks the right {@link TextExtractor} for a file (by content type, falling back to
 * filename) and runs it. New file types are added by dropping in another {@code @Component}
 * implementing {@link TextExtractor} -- nothing here has to change.
 */
@Service
public class TextExtractionService {

    private final List<TextExtractor> extractors;
    private final TextExtractor fallback;

    public TextExtractionService(List<TextExtractor> extractors, PlainTextExtractor fallback) {
        this.extractors = extractors;
        this.fallback = fallback;
    }

    public String extract(byte[] data, String contentType, String filename) throws IOException {
        for (TextExtractor extractor : extractors) {
            if (extractor.supports(contentType, filename)) {
                return extractor.extract(data);
            }
        }
        // Unknown type: best-effort as plain text rather than failing the whole pipeline.
        return fallback.extract(data);
    }
}
