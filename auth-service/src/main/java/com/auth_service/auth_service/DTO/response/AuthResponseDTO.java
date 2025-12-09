package com.auth_service.auth_service.DTO.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType;
    private Long expireId;
}
