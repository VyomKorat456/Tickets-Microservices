package com.comment_service.comment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "ticket_comments", indexes = {
        @Index(name = "idx_ticket_comments_ticket_id", columnList = "ticket_id"),
        @Index(name = "idx_ticket_comments_author_user_id", columnList = "author_user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    // snapshot of username at time of comment
    @Column(name = "author_user_name", length = 255)
    private String authorUserName;

    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}

