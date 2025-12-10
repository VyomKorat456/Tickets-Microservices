package com.ticket_service.ticket_service.DTO.ticketDTO.request;

import com.ticket_service.ticket_service.enums.TicketStatus;
import lombok.Data;

@Data
public class TicketStatusUpdateRequestDTO {
    private TicketStatus status;
}
