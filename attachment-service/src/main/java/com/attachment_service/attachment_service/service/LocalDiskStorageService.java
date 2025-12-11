package com.attachment_service.attachment_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface LocalDiskStorageService {
    String store(Long ticketId, MultipartFile file) throws IOException;

    Path resolve(String relativePath);
}
