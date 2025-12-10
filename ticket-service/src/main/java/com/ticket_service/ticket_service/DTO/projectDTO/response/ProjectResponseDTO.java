package com.ticket_service.ticket_service.DTO.projectDTO.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Builder
public class ProjectResponseDTO {
    private Long id;
    private String key;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
