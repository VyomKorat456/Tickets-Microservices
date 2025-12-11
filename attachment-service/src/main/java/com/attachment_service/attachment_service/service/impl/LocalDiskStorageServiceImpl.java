package com.attachment_service.attachment_service.service.impl;

import com.attachment_service.attachment_service.service.LocalDiskStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalDiskStorageServiceImpl implements LocalDiskStorageService {
    private final Path baseDir;

    public LocalDiskStorageServiceImpl(@Value("${app.attachments.base-dir}") String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage base directory", e);
        }
    }
    @Override
    public String store(Long ticketId, MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (original.contains("..")) {
            throw new IOException("Invalid path sequence in file name: " + original);
        }

        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);

        String generated = System.currentTimeMillis() + "-" + UUID.randomUUID() + ext;
        Path ticketDir = baseDir.resolve(String.valueOf(ticketId));
        Files.createDirectories(ticketDir);

        Path target = ticketDir.resolve(generated);
        // atomic move: copy then move
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // return relative path (posix style)
        Path relative = baseDir.relativize(target);
        return relative.toString().replace("\\", "/");
    }

    @Override
    public Path resolve(String relativePath) {
        Path resolved = baseDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new RuntimeException("Resolved path outside base dir");
        }
        return resolved;
    }
}
