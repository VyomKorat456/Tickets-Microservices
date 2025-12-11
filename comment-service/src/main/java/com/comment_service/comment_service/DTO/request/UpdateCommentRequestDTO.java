package com.comment_service.comment_service.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequestDTO {
    @NotBlank
    private String content;
}
