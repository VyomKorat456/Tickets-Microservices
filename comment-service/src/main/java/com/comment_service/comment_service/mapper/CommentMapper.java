package com.comment_service.comment_service.mapper;

import com.comment_service.comment_service.DTO.response.CommentResponseDTO;
import com.comment_service.comment_service.entity.TicketComment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponseDTO toResponse(TicketComment c) {
        if (c == null) return null;

        return new CommentResponseDTO(
                c.getId(),
                c.getTicketId(),
                c.getAuthorUserId(),
                c.getAuthorUserName(),
                c.getContent(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
