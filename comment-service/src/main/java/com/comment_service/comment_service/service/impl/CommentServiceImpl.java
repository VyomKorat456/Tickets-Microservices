package com.comment_service.comment_service.service.impl;

import com.comment_service.comment_service.DTO.request.CreateCommentRequestDTO;
import com.comment_service.comment_service.DTO.request.UpdateCommentRequestDTO;
import com.comment_service.comment_service.DTO.response.CommentResponseDTO;
import com.comment_service.comment_service.entity.TicketComment;
import com.comment_service.comment_service.exception.ForbiddenException;
import com.comment_service.comment_service.exception.NotFoundException;
import com.comment_service.comment_service.mapper.CommentMapper;
import com.comment_service.comment_service.repository.TicketCommentRepository;
import com.comment_service.comment_service.security.JwtUtil;
import com.comment_service.comment_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final JwtUtil jwtUtil;
    private final TicketCommentRepository ticketCommentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDTO create(String authorizationHeader, CreateCommentRequestDTO createCommentRequestDTO) {
        log.info("comes in service");
        String token = extractTokenFromHeader(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            log.info("failed auth");
            throw new ForbiddenException("Invalid or missing JWT token");
        }

        Long userId = jwtUtil.extractUserId(token);
        log.info("added userId {userId}"+userId);
        String username = jwtUtil.extractUsername(token);

        TicketComment comment = TicketComment.builder()
                .ticketId(createCommentRequestDTO.getTicketId())
                .content(createCommentRequestDTO.getContent())
                .authorUserId(userId)
                .authorUserName(username)
                .createdAt(Instant.now())
                .build();

        TicketComment saved = ticketCommentRepository.save(comment);
        return commentMapper.toResponse(saved);
    }

    @Override
    public List<CommentResponseDTO> listByTicket(Long ticketId) {
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream().map(commentMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDTO update(String authorizationHeader, Long commentId, UpdateCommentRequestDTO updateCommentRequestDTO) {
        String token = extractTokenFromHeader(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new ForbiddenException("Invalid or missing JWT token");
        }

        Long userId = jwtUtil.extractUserId(token);
        List<String> roles = jwtUtil.extractRoles(token);

        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

        if (!canModify(userId, roles, comment)) throw new ForbiddenException("Not allowed to edit comment");

        comment.setContent(updateCommentRequestDTO.getContent());
        comment.setUpdatedAt(Instant.now());
        TicketComment updated = ticketCommentRepository.save(comment);
        return commentMapper.toResponse(updated);
    }

    @Override
    public void delete(String authorizationHeader, Long commentId) {
        String token = extractTokenFromHeader(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new ForbiddenException("Invalid or missing JWT token");
        }

        Long userId = jwtUtil.extractUserId(token);
        List<String> roles = jwtUtil.extractRoles(token);

        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

        if (!canModify(userId, roles, comment)) throw new ForbiddenException("Not allowed to delete comment");

        ticketCommentRepository.delete(comment);
    }

    //helper class
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null) return null;
        return authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
    }

    private boolean canModify(Long tokenUserId, List<String> roles, TicketComment comment) {
        if (tokenUserId != null && Objects.equals(tokenUserId, comment.getAuthorUserId())) return true;
        if (roles != null) {
            // admin or admin_role allow.....
            for (String r : roles) {
                if ("ADMIN".equalsIgnoreCase(r) || "ROLE_ADMIN".equalsIgnoreCase(r)) return true;
            }
        }
        return false;
    }
}
