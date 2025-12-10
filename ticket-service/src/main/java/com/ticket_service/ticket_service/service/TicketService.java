package com.ticket_service.ticket_service.service;

import com.ticket_service.ticket_service.DTO.ticketDTO.request.*;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketHistoryResponseDTO;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketResponseDTO;
import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketStatus;
import com.ticket_service.ticket_service.enums.TicketType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TicketService {
    TicketResponseDTO createTicket(TicketCreateRequestDTO ticketCreateRequestDTO, Long creatorUserId);

    TicketResponseDTO getTicketById(Long ticketId);

    Page<TicketResponseDTO> searchTickets(
            TicketStatus ticketStatus,
            TicketPriority ticketPriority,
            TicketType ticketType,
            Long projectId,
            Long creatorUserId,
            Long assignedUserId,
            int page,
            int size
    );

    TicketResponseDTO updateTicketBasicInfo(Long ticketId,
                                         TicketUpdateRequestDTO ticketUpdateRequestDTO,
                                         Long currentUserId,
                                         boolean isAdmin);

    TicketResponseDTO assignTicket(Long ticketId,
                                TicketAssignRequestDTO ticketAssignRequestDTO,
                                Long currentUserId,
                                String currentUserName);

    TicketResponseDTO changeStatus(Long ticketId,
                                TicketStatusUpdateRequestDTO ticketStatusUpdateRequestDTO,
                                Long currentUserId,
                                String currentUserName,
                                boolean isAdmin);

    TicketResponseDTO cloneTicket(Long ticketId,
                               TicketCloneRequestDTO ticketCloneRequestDTO,
                               Long currentUserId,
                               String currentUserName);

    List<TicketHistoryResponseDTO> getTicketHistory(Long ticketId);
}
