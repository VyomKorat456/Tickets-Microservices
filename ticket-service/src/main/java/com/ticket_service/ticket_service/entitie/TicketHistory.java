package com.ticket_service.ticket_service.entitie;

import com.ticket_service.ticket_service.enums.TicketChangeType;
import com.ticket_service.ticket_service.enums.TicketPriority;
import com.ticket_service.ticket_service.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_history")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No relation to User entity, only IDs
    @Column(nullable = false)
    private Long ticketId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TicketChangeType changeType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketStatus newStatus;

    private Long oldAssignedUserId;
    private Long newAssignedUserId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketPriority oldPriority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketPriority newPriority;

    @Column(nullable = false)
    private Long changedByUserId;

    @Column(length = 255)
    private String changedByUserName; // snapshot, optional

    @Column(length = 500)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant changedAt;
}
