package com.auth_service.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Controller {
    @GetMapping("/test")
    public String get(){
        return "Auth Service is Running with postgres";
    }
}
