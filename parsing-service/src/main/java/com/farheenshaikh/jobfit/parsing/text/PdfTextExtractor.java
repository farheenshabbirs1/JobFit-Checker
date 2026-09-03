package com.farheenshaikh.jobfit.parsing.text;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Handles .pdf uploads via Apache PDFBox -- the common case for resumes. */
@Component
@Order(0)
public class PdfTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String contentType, String filename) {
        if ("application/pdf".equalsIgnoreCase(contentType)) {
            return true;
        }
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }

    @Override
    public String extract(byte[] data) throws IOException {
        try (PDDocument document = Loader.loadPDF(data)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }
}
