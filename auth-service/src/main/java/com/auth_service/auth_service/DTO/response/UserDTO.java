package com.auth_service.auth_service.DTO.response;

import com.auth_service.auth_service.enums.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDTO(Long id, String fullName, String email, Role role, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
