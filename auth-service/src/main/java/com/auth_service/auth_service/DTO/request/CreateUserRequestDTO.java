package com.auth_service.auth_service.DTO.request;

import com.auth_service.auth_service.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CreateUserRequestDTO {
    private String fullName;
    private String email;
    private String password;  // raw password from client
    private Role role;
}
