package com.auth_service.auth_service.email;

import lombok.Data;

@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String body;
}
