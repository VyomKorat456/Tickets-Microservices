package com.attachment_service.attachment_service.DTO.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UploadAttachmentRequestDTO {
    @NotNull
    @Positive(message = "ticketId must be positive")
    private Long ticketId;
}
