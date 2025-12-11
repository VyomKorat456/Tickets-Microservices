package com.attachment_service.attachment_service.mapper;

import com.attachment_service.attachment_service.DTO.response.AttachmentResponseDTO;
import com.attachment_service.attachment_service.entity.TicketAttachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {
    public AttachmentResponseDTO toDto(TicketAttachment ticketAttachment) {
        return new AttachmentResponseDTO(
                ticketAttachment.getId(),
                ticketAttachment.getTicketId(),
                ticketAttachment.getFileName(),
                ticketAttachment.getFilePath(),
                ticketAttachment.getFileSize(),
                ticketAttachment.getContentType(),
                ticketAttachment.getUploadedByUserId(),
                ticketAttachment.getUploadedAt()
        );
    }
}
