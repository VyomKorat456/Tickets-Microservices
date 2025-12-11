package com.attachment_service.attachment_service.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadAttachmentRequestDTO {
    @NotNull
    private Long ticketId;
}
