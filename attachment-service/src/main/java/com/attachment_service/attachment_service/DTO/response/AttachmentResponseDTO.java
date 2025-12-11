package com.attachment_service.attachment_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AttachmentResponseDTO {
    private Long id;
    private Long ticketId;
    private String fileName;
    private String filePath; // relative path saved in DB
    private Long fileSize;
    private String contentType;
    private Long uploadedByUserId;
    private Instant uploadedAt;
}
