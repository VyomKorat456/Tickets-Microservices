package com.comment_service.comment_service.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequestDTO {
    @NotNull
    private Long ticketId;
    @NotBlank
    private String content;
}
