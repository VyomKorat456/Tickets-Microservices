package com.auth_service.auth_service.DTO.request;

import com.auth_service.auth_service.enums.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
public class CreateUserRequestDTO {
    private String fullName;
    private String email;
    private String password;  // raw password from client
    private Role role;
}
