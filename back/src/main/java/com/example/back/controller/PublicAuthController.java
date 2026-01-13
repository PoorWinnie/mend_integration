package com.example.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.MessageResponse;
import com.example.back.dto.RegisterRequest;
import com.example.back.domain.Role;
import com.example.back.domain.UserBean;
import com.example.back.repository.RoleRepository;
import com.example.back.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Valid;

/**
 * 公開註冊控制器 - 提供不需要認證的註冊端點
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
public class PublicAuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    /**
     * 公開註冊端點 - 不需要認證
     * 移除 @PreAuthorize 註解以避免衝突
     */
    @PostMapping("/register")
    public ResponseEntity<?> publicRegisterUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // 添加除錯日誌
        System.out.println("接收到公開註冊請求: " + registerRequest.getUsername() + ", " + registerRequest.getEmail());
        
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        
        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        
        // 創建新用戶
        UserBean user = new UserBean();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setStatus(1); // 自動啟用狀態
        user.setIsActive(true); // 自動啟用
        
        // 設置用戶角色
        Role userRole;
        
        if ("owner".equals(registerRequest.getRole()) || "CAMP_OWNER".equals(registerRequest.getRole())) {
            userRole = roleRepository.findByRoleName("ROLE_CAMP_OWNER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }
        
        user.getRoles().add(userRole);
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}