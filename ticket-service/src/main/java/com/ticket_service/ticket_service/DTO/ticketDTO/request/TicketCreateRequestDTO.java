package com.ticket_service.ticket_service.DTO.ticketDTO.request;

import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketType;
import lombok.Data;

@Data
public class TicketCreateRequestDTO {
    private String title;
    private String description;
    private TicketType type;
    private TicketPriority priority;
    private Long projectId;
}
