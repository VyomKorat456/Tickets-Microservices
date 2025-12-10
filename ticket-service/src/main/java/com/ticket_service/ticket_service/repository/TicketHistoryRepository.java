package com.ticket_service.ticket_service.repository;

import com.ticket_service.ticket_service.entitie.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByTicketIdOrderByChangedAtAsc(Long ticketId);
}
