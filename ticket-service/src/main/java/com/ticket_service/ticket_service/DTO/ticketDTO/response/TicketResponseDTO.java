package com.ticket_service.ticket_service.DTO.ticketDTO.response;

import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketStatus;
import com.ticket_service.ticket_service.enums.TicketType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TicketType type;
    private TicketPriority priority;
    private TicketStatus status;
    private Long projectId;
    private Long creatorUserId;
    private Long assignedUserId;
    private Long originalTicketId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private Instant closedAt;
}
