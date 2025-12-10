package com.ticket_service.ticket_service.service.impl;

import com.ticket_service.ticket_service.DTO.ticketDTO.request.*;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketHistoryResponseDTO;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketResponseDTO;
import com.ticket_service.ticket_service.entitie.Project;
import com.ticket_service.ticket_service.entitie.Ticket;
import com.ticket_service.ticket_service.entitie.TicketHistory;
import com.ticket_service.ticket_service.enums.TicketChangeType;
import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketStatus;
import com.ticket_service.ticket_service.enums.TicketType;
import com.ticket_service.ticket_service.mapper.TicketMapper;
import com.ticket_service.ticket_service.repository.ProjectRepository;
import com.ticket_service.ticket_service.repository.TicketHistoryRepository;
import com.ticket_service.ticket_service.repository.TicketRepository;
import com.ticket_service.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    //helpers
    private Ticket getTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    @Override
    public TicketResponseDTO createTicket(TicketCreateRequestDTO ticketCreateRequestDTO, Long creatorUserId) {
        Project project = projectRepository.findById(ticketCreateRequestDTO.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid projectId"));

        Ticket ticket = Ticket.builder()
                .title(ticketCreateRequestDTO.getTitle())
                .description(ticketCreateRequestDTO.getDescription())
                .type(ticketCreateRequestDTO.getType())
                .priority(ticketCreateRequestDTO.getPriority())
                .status(TicketStatus.OPEN)
                .project(project)
                .creatorUserId(creatorUserId)
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return TicketMapper.toTicketResponseDTO(saved);
    }

    @Override
    public TicketResponseDTO getTicketById(Long ticketId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        return TicketMapper.toTicketResponseDTO(ticket);
    }

    @Override
    public Page<TicketResponseDTO> searchTickets(TicketStatus ticketStatus, TicketPriority ticketPriority, TicketType ticketType, Long projectId, Long creatorUserId, Long assignedUserId, int page, int size) {
        Specification<Ticket> specification = Specification.where(null);

        if (ticketStatus != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), ticketStatus));
        }
        if (ticketPriority != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("priority"), ticketPriority));
        }
        if (ticketType != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("type"), ticketType));
        }
        if (projectId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
        }
        if (creatorUserId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("creatorUserId"), creatorUserId));
        }
        if (assignedUserId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("assignedUserId"), assignedUserId));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Ticket> ticketPage = ticketRepository.findAll(specification, pageable);

        List<TicketResponseDTO> responses = ticketPage.getContent().stream()
                .map(TicketMapper::toTicketResponseDTO)
                .toList();

        return new PageImpl<>(responses, pageable, ticketPage.getTotalElements());

    }

    @Override
    public TicketResponseDTO updateTicketBasicInfo(Long ticketId, TicketUpdateRequestDTO ticketUpdateRequestDTO, Long currentUserId, boolean isAdmin) {
        Ticket ticket = getTicketOrThrow(ticketId);

        if (!isAdmin && !ticket.getCreatorUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only creator or admin can update ticket");
        }

        ticket.setTitle(ticketUpdateRequestDTO.getTitle());
        ticket.setDescription(ticketUpdateRequestDTO.getDescription());
        ticket.setType(ticketUpdateRequestDTO.getType());
        ticket.setPriority(ticketUpdateRequestDTO.getPriority());

        Ticket saved = ticketRepository.save(ticket);
        return TicketMapper.toTicketResponseDTO(saved);
    }

    @Override
    public TicketResponseDTO assignTicket(Long ticketId, TicketAssignRequestDTO ticketAssignRequestDTO, Long currentUserId, String currentUserName) {

        Ticket ticket = getTicketOrThrow(ticketId);

        Long oldAssignee = ticket.getAssignedUserId();
        ticket.setAssignedUserId(ticketAssignRequestDTO.getAssignedUserId());
        Ticket saved = ticketRepository.save(ticket);

        TicketHistory history = TicketHistory.builder()
                .ticketId(saved.getId())
                .changeType(TicketChangeType.ASSIGNEE_CHANGE)
                .oldAssignedUserId(oldAssignee)
                .newAssignedUserId(ticketAssignRequestDTO.getAssignedUserId())
                .changedByUserId(currentUserId)
                .changedByUserName(currentUserName)
                .note("Assignee changed")
                .build();
        ticketHistoryRepository.save(history);

        return TicketMapper.toTicketResponseDTO(saved);
    }

    @Override
    public TicketResponseDTO changeStatus(Long ticketId, TicketStatusUpdateRequestDTO ticketStatusUpdateRequestDTO, Long currentUserId, String currentUserName, boolean isAdmin) {
        Ticket ticket = getTicketOrThrow(ticketId);
        TicketStatus oldStatus = ticket.getStatus();
        TicketStatus newStatus = ticketStatusUpdateRequestDTO.getStatus();

        if (!isValidTransition(oldStatus, newStatus, isAdmin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition");
        }

        ticket.setStatus(newStatus);

        Instant now = Instant.now();
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(now);
        }
        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(now);
        }
        if (newStatus == TicketStatus.REOPENED) {
            ticket.setClosedAt(null);
            ticket.setResolvedAt(null);
        }

        Ticket saved = ticketRepository.save(ticket);

        TicketHistory history = TicketHistory.builder()
                .ticketId(saved.getId())
                .changeType(TicketChangeType.STATUS_CHANGE)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedByUserId(currentUserId)
                .changedByUserName(currentUserName)
                .note("Status changed from " + oldStatus + " to " + newStatus)
                .build();
        ticketHistoryRepository.save(history);

        return TicketMapper.toTicketResponseDTO(saved);
    }

    @Override
    public TicketResponseDTO cloneTicket(Long ticketId, TicketCloneRequestDTO ticketCloneRequestDTO, Long currentUserId, String currentUserName) {
        Ticket original = getTicketOrThrow(ticketId);

        Project targetProject = original.getProject();
        if (ticketCloneRequestDTO.getProjectId() != null && !ticketCloneRequestDTO.getProjectId().equals(original.getProject().getId())) {
            targetProject = projectRepository.findById(ticketCloneRequestDTO.getProjectId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid projectId"));
        }

        TicketPriority newPriority = ticketCloneRequestDTO.getPriority() != null
                ? ticketCloneRequestDTO.getPriority()
                : original.getPriority();

        Ticket clone = Ticket.builder()
                .title(original.getTitle())
                .description(original.getDescription())
                .type(original.getType())
                .priority(newPriority)
                .status(TicketStatus.OPEN)
                .project(targetProject)
                .creatorUserId(currentUserId)
                .originalTicketId(original.getId())
                .build();

        Ticket saved = ticketRepository.save(clone);

        TicketHistory history = TicketHistory.builder()
                .ticketId(saved.getId())
                .changeType(TicketChangeType.STATUS_CHANGE)
                .oldStatus(null)
                .newStatus(TicketStatus.OPEN)
                .changedByUserId(currentUserId)
                .changedByUserName(currentUserName)
                .note("Ticket cloned from #" + original.getId())
                .build();
        ticketHistoryRepository.save(history);

        return TicketMapper.toTicketResponseDTO(saved);
    }

    @Override
    public List<TicketHistoryResponseDTO> getTicketHistory(Long ticketId) {
        getTicketOrThrow(ticketId);

        return ticketHistoryRepository.findByTicketIdOrderByChangedAtAsc(ticketId)
                .stream()
                .map(TicketMapper::toTicketHistoryResponseDTO)
                .toList();
    }

    private boolean isValidTransition(TicketStatus from, TicketStatus to, boolean isAdmin) {
        if (from == to) return true;

        switch (from) {
            case OPEN:
                return to == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS:
                return to == TicketStatus.RESOLVED || to == TicketStatus.OPEN;
            case RESOLVED:
                return to == TicketStatus.CLOSED || to == TicketStatus.REOPENED;
            case CLOSED:
                return isAdmin && to == TicketStatus.REOPENED;
            case REOPENED:
                return to == TicketStatus.IN_PROGRESS || to == TicketStatus.RESOLVED;
            default:
                return false;
        }
    }
}
