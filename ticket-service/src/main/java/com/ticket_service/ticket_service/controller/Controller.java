package com.ticket_service.ticket_service.controller;

import com.ticket_service.ticket_service.DTO.ticketDTO.request.*;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketHistoryResponseDTO;
import com.ticket_service.ticket_service.DTO.ticketDTO.response.TicketResponseDTO;
import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketStatus;
import com.ticket_service.ticket_service.enums.TicketType;
import com.ticket_service.ticket_service.security.JwtUtil;
import com.ticket_service.ticket_service.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class Controller {
    private final TicketService ticketService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public TicketResponseDTO createTicket(@RequestBody TicketCreateRequestDTO request,
                                          HttpServletRequest httpRequest) {

        log.info("=== POST /ticket ===");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logSecurityContext(auth);

        Long userId = extractUserIdFromToken(httpRequest);
        return ticketService.createTicket(request, userId);
    }

    @GetMapping("/{ticketId}")
    public TicketResponseDTO getTicketById(@PathVariable Long ticketId) {
        log.info("=== GET /ticket/{} ===", ticketId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logSecurityContext(auth);
        
        return ticketService.getTicketById(ticketId);
    }

    @GetMapping
    public Page<TicketResponseDTO> searchTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) TicketType type,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long creatorUserId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("=== GET /ticket ===");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logSecurityContext(auth);
        
        return ticketService.searchTickets(status, priority, type,
                projectId, creatorUserId, assignedUserId, page, size);
    }

    @PutMapping("/{ticketId}")
    public TicketResponseDTO updateTicketBasicInfo(@PathVariable Long ticketId,
                                                @RequestBody TicketUpdateRequestDTO request,
                                                HttpServletRequest httpRequest) {

        Long userId = extractUserIdFromToken(httpRequest);
        boolean isAdmin = hasRole("ADMIN");
        return ticketService.updateTicketBasicInfo(ticketId, request, userId, isAdmin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ticketId}/assign")
    public TicketResponseDTO assignTicket(@PathVariable Long ticketId,
                                       @RequestBody TicketAssignRequestDTO request,
                                       HttpServletRequest httpRequest) {

        Long userId = extractUserIdFromToken(httpRequest);
        String username = extractUsernameFromToken(httpRequest);

        return ticketService.assignTicket(ticketId, request, userId, username);
    }

    @PutMapping("/{ticketId}/status")
    public TicketResponseDTO changeStatus(@PathVariable Long ticketId,
                                       @RequestBody TicketStatusUpdateRequestDTO request,
                                       HttpServletRequest httpRequest) {

        Long userId = extractUserIdFromToken(httpRequest);
        String username = extractUsernameFromToken(httpRequest);
        boolean isAdmin = hasRole("ADMIN");

        return ticketService.changeStatus(ticketId, request, userId, username, isAdmin);
    }

    @PostMapping("/{ticketId}/clone")
    public TicketResponseDTO cloneTicket(@PathVariable Long ticketId,
                                      @RequestBody(required = false) TicketCloneRequestDTO request,
                                      HttpServletRequest httpRequest) {

        if (request == null) {
            request = new TicketCloneRequestDTO();
        }

        Long userId = extractUserIdFromToken(httpRequest);
        String username = extractUsernameFromToken(httpRequest);

        return ticketService.cloneTicket(ticketId, request, userId, username);
    }

    @GetMapping("/{ticketId}/history")
    public List<TicketHistoryResponseDTO> getTicketHistory(@PathVariable Long ticketId) {
        return ticketService.getTicketHistory(ticketId);
    }

   //helpers

    private Long extractUserIdFromToken(HttpServletRequest request) {
        String token = resolveToken(request);
        return jwtUtil.extractUserId(token);
    }

    private String extractUsernameFromToken(HttpServletRequest request) {
        String token = resolveToken(request);
        return jwtUtil.extractUsername(token);
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        String roleName = "ROLE_" + role;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (roleName.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private void logSecurityContext(Authentication auth) {
        if (auth == null) {
            log.warn("❌ Authentication is NULL in SecurityContext");
        } else {
            log.info("✓ Authentication Principal: {}", auth.getPrincipal());
            log.info("✓ Authentication Authenticated: {}", auth.isAuthenticated());
            List<String> authorities = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            log.info("✓ Granted Authorities: {}", authorities);
        }
    }
}


