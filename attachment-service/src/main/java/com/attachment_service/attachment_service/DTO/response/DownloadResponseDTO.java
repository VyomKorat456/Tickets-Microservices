package com.attachment_service.attachment_service.DTO.response;

import io.opentelemetry.sdk.resources.Resource;
import lombok.Getter;

@Getter
public class DownloadResponseDTO {
    private final Resource resource;
    private final String contentType;
    private final String fileName;

    public DownloadResponseDTO(Resource resource, String contentType, String fileName) {
        this.resource = resource;
        this.contentType = contentType;
        this.fileName = fileName;
    }

}
