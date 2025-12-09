package com.auth_service.auth_service.DTO.request;

import com.auth_service.auth_service.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequestDTO {
    private String fullName;
    private Role role;
    private Boolean active;
    private String email;
}
