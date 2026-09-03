package com.farheenshaikh.jobfit.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Default {@link BlobStorage}: files live under one directory on disk. In
 * {@code docker-compose.yml} that directory is a named volume shared by api-service and
 * parsing-service (the two services that actually touch file bytes), which is what makes
 * "upload on one container, parse on another" work without a cloud object store.
 */
@Component
public class LocalFileBlobStorage implements BlobStorage {

    private final Path rootDir;

    public LocalFileBlobStorage(@Value("${jobfit.storage.root:/var/jobfit/resumes}") String rootDir) {
        this.rootDir = Path.of(rootDir);
        try {
            Files.createDirectories(this.rootDir);
        } catch (IOException e) {
            throw new IllegalStateException("could not create blob storage root " + this.rootDir, e);
        }
    }

    @Override
    public String store(String suggestedName, byte[] data) throws IOException {
        String key = UUID.randomUUID() + "-" + sanitize(suggestedName);
        Path target = rootDir.resolve(key);
        Files.write(target, data);
        return key;
    }

    @Override
    public byte[] read(String key) throws IOException {
        return Files.readAllBytes(resolve(key));
    }

    @Override
    public InputStream openStream(String key) throws IOException {
        return Files.newInputStream(resolve(key));
    }

    private Path resolve(String key) throws IOException {
        Path resolved = rootDir.resolve(key).normalize();
        if (!resolved.startsWith(rootDir)) {
            throw new IOException("invalid storage key: " + key);
        }
        return resolved;
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
