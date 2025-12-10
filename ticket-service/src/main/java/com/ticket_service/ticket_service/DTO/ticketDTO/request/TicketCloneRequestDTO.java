package com.ticket_service.ticket_service.DTO.ticketDTO.request;

import com.ticket_service.ticket_service.enums.TicketPriority;
import lombok.Data;

@Data
public class TicketCloneRequestDTO {
    private Long projectId;
    private TicketPriority priority;
}
