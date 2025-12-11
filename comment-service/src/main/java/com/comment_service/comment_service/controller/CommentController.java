package com.comment_service.comment_service.controller;

import com.comment_service.comment_service.DTO.request.CreateCommentRequestDTO;
import com.comment_service.comment_service.DTO.request.UpdateCommentRequestDTO;
import com.comment_service.comment_service.DTO.response.CommentResponseDTO;
import com.comment_service.comment_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/by")
    public ResponseEntity<CommentResponseDTO> create(
            @RequestHeader(name = "Authorization") String authorization,
            @Validated @RequestBody CreateCommentRequestDTO createCommentRequestDTO) {
        log.info("request comes");
        CommentResponseDTO commentResponseDTO = commentService.create(authorization, createCommentRequestDTO);
        return ResponseEntity.status(201).body(commentResponseDTO);
    }

    @GetMapping("/by-ticket/{ticketId}")
    public ResponseEntity<List<CommentResponseDTO>> listByTicket(
            @PathVariable Long ticketId,
            @RequestHeader(name = "Authorization", required = false) String authorization // optional
    ) {
        return ResponseEntity.ok(commentService.listByTicket(ticketId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable Long commentId,
            @Validated @RequestBody UpdateCommentRequestDTO updateCommentRequestDTO) {
        return ResponseEntity.ok(commentService.update(authorization, commentId, updateCommentRequestDTO));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable Long commentId) {
        commentService.delete(authorization, commentId);
        return ResponseEntity.noContent().build();
    }
}
