package com.farheenshaikh.jobfit.common.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Where uploaded resume files actually live. api-service writes through this on upload;
 * parsing-service reads through it before extracting text. Neither depends on which
 * implementation is wired up -- {@link LocalFileBlobStorage} is the default (a shared
 * volume/disk path, which is all a portfolio-scale deployment needs), and a cloud-backed
 * implementation is a drop-in swap behind this interface if one is ever needed.
 */
public interface BlobStorage {

    /** Stores {@code data} under a fresh key and returns that key. */
    String store(String suggestedName, byte[] data) throws IOException;

    /** Reads back the bytes stored under {@code key}. */
    byte[] read(String key) throws IOException;

    /** Streams the bytes stored under {@code key}, for large files. */
    InputStream openStream(String key) throws IOException;
}
