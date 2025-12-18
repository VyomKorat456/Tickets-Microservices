package com.attachment_service.attachment_service.repository;

import com.attachment_service.attachment_service.entity.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {
    List<TicketAttachment> findByTicketId(Long ticketId);

    List<TicketAttachment> findByTicketIdOrderByUploadedAtAsc(Long ticketId);
}
