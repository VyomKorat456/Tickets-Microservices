package com.ticket_service.ticket_service.DTO.projectDTO.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectCreateRequestDTO {
    private String key;
    private String name;
    private String description;
}
