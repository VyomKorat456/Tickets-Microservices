package com.attachment_service.attachment_service.entity;

import jakarta.persistence.*;
//import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ticket_attachments")
public class TicketAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    // This will store the relative path under baseDir (e.g., ticketId/12345-filename.jpg)
    @Column(name = "file_path", nullable = false, length = 2000)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;
}
