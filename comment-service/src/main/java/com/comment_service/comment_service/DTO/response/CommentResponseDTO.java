package com.comment_service.comment_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long ticketId;
    private Long authorUserId;
    private String authorUserName;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
