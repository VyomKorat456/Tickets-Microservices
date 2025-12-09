package com.auth_service.auth_service.DTO.request;

import jakarta.inject.Named;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class LoginRequestDTO {
    private String email;
    private String password;
}
