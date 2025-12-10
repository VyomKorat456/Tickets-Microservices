package com.ticket_service.ticket_service.mapper;

import com.ticket_service.ticket_service.DTO.projectDTO.response.ProjectResponseDTO;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketHistoryResponseDTO;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketResponseDTO;
import com.ticket_service.ticket_service.entitie.Project;
import com.ticket_service.ticket_service.entitie.Ticket;
import com.ticket_service.ticket_service.entitie.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    private TicketMapper() {}

    public static ProjectResponseDTO toProjectResponseDTO(Project project) {
        if (project == null) return null;

        return ProjectResponseDTO.builder()
                .id(project.getId())
                .key(project.getKey())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
    public static TicketResponseDTO toTicketResponseDTO(Ticket ticket) {
        if (ticket == null) return null;

        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .type(ticket.getType())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .projectId(ticket.getProject() != null ? ticket.getProject().getId() : null)
                .creatorUserId(ticket.getCreatorUserId())
                .assignedUserId(ticket.getAssignedUserId())
                .originalTicketId(ticket.getOriginalTicketId())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .build();
    }

    public static TicketHistoryResponseDTO toTicketHistoryResponseDTO(TicketHistory history) {
        if (history == null) return null;

        return TicketHistoryResponseDTO.builder()
                .id(history.getId())
                .ticketId(history.getTicketId())
                .changeType(history.getChangeType())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .oldAssignedUserId(history.getOldAssignedUserId())
                .newAssignedUserId(history.getNewAssignedUserId())
                .oldPriority(history.getOldPriority())
                .newPriority(history.getNewPriority())
                .changedByUserId(history.getChangedByUserId())
                .changedByUserName(history.getChangedByUserName())
                .note(history.getNote())
                .changedAt(history.getChangedAt())
                .build();
    }

}
