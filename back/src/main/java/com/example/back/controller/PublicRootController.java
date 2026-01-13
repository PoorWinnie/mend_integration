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

import com.example.back.dto.RegisterRequest;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/public")
public class PublicRootController {
    private static final Logger logger = LoggerFactory.getLogger(PublicRootController.class);
    
    @Autowired
    private PublicAuthController publicAuthController;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("從根路徑收到註冊請求: {}", registerRequest.getUsername());
        // 轉發請求到主公開控制器
        return publicAuthController.publicRegisterUser(registerRequest);
    }
}