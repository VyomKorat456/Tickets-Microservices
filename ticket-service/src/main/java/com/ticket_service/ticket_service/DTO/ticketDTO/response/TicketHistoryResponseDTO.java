package com.ticket_service.ticket_service.DTO.ticketDTO.response;

import com.ticket_service.ticket_service.enums.TicketChangeType;
import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketHistoryResponseDTO {
    private Long id;
    private Long ticketId;
    private TicketChangeType changeType;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private Long oldAssignedUserId;
    private Long newAssignedUserId;
    private TicketPriority oldPriority;
    private TicketPriority newPriority;
    private Long changedByUserId;
    private String changedByUserName;
    private String note;
    private Instant changedAt;
}
