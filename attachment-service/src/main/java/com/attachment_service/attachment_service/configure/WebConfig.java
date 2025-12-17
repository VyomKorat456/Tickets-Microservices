package com.attachment_service.attachment_service.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Must match the storage base dir used by LocalDiskStorageServiceImpl.
     * Example: C:/Users/dell/Desktop/Frontend/ff/attachment-service/src/main/resources/uploads
     */
    @Value("${app.attachments.base-dir}")
    private String baseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure trailing slash, and use forward slashes for file: URI compatibility.
        Path p = Paths.get(baseDir).toAbsolutePath().normalize();
        String folder = "file:" + p.toString().replace("\\", "/") + "/";

        // Serve stored files under /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(folder);

        // Backwards-compat: some older code might request /uplods/** (typo)
        registry.addResourceHandler("/uplods/**")
                .addResourceLocations(folder);
    }
}
