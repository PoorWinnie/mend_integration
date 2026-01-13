package com.example.back.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.LoginRequest;
import com.example.back.dto.RegisterRequest;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AltAuthController {
    private static final Logger logger = LoggerFactory.getLogger(AltAuthController.class);
    
    @Autowired
    private AuthController authController;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("從/auth/login收到登入請求: {}", loginRequest.getUsername());
        // 將請求轉發到主控制器
        return authController.authenticateUser(loginRequest);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("從/auth/register收到註冊請求: {}", registerRequest.getUsername());
        // 轉發到主認證控制器
        return authController.registerUser(registerRequest);
    }
}