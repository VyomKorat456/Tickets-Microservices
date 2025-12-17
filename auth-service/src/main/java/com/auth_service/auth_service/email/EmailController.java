package com.auth_service.auth_service.email;

import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/sent")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO){
        emailService.sendEmail(emailRequestDTO);
        return ResponseEntity.ok("Email sent successfully");
    }
}
