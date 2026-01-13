package com.example.back.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.JwtResponse;
import com.example.back.dto.LoginRequest;
import com.example.back.dto.MessageResponse;
import com.example.back.domain.UserBean;
import com.example.back.repository.UserRepository;
import com.example.back.security.jwt.JwtUtils;
import com.example.back.service.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthController.class);
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("管理員登入請求: {}", loginRequest.getUsername());
        
        // 在認證前檢查用戶角色
        Optional<UserBean> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            UserBean user = userOpt.get();
            boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> 
                    "ROLE_ADMIN".equals(role.getRoleName()) || 
                    "ROLE_SUPER_ADMIN".equals(role.getRoleName()));
            
            if (!isAdmin) {
                logger.warn("非管理員帳號 {} 嘗試登入管理後台", loginRequest.getUsername());
                return ResponseEntity.status(403).body(new MessageResponse("非管理員帳號，無法登入管理後台"));
            }
        }
        
        // 認證用戶
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // 檢查是否有管理員角色
        boolean isAdmin = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_ADMIN"));
        
        if (!isAdmin) {
            logger.warn("非管理員帳號 {} 通過認證但被管理後台拒絕", loginRequest.getUsername());
            return ResponseEntity.status(403).body(new MessageResponse("非管理員帳號，無法登入管理後台"));
        }
        
        // 生成JWT令牌
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority().replace("ROLE_", ""))
            .collect(Collectors.toList());
        
        logger.info("管理員 {} 登入成功，角色: {}", userDetails.getUsername(), roles);
        
        return ResponseEntity.ok(new JwtResponse(
            jwt, 
            refreshToken,
            userDetails.getId(), 
            userDetails.getUsername(), 
            userDetails.getEmail(), 
            roles));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutAdmin() {
        logger.info("管理員登出");
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("管理員已登出，請重新登入"));
    }
}