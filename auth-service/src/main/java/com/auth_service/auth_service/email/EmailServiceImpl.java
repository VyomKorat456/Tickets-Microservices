package com.auth_service.auth_service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailRequestDTO emailRequestDTO) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailRequestDTO.getTo());
            message.setSubject(emailRequestDTO.getSubject());
            message.setText(emailRequestDTO.getBody());
            javaMailSender.send(message);
            log.info("email sent successfully");
        }catch (Exception e){
            log.error("failed failed failed");
            throw new RuntimeException("Email Sending failed");
        }
    }
}
