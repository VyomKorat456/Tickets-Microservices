package com.comment_service.comment_service.service;

import com.comment_service.comment_service.DTO.request.CreateCommentRequestDTO;
import com.comment_service.comment_service.DTO.request.UpdateCommentRequestDTO;
import com.comment_service.comment_service.DTO.response.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CommentService {
    CommentResponseDTO create(String authorizationHeader, CreateCommentRequestDTO createCommentRequestDTO);

    List<CommentResponseDTO> listByTicket(Long ticketId);

    CommentResponseDTO update(String authorizationHeader, Long commentId, UpdateCommentRequestDTO updateCommentRequestDTO);

    void delete(String authorizationHeader, Long commentId);
}
