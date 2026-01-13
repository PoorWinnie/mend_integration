package com.example.back.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.JwtResponse;
import com.example.back.dto.LoginRequest;
import com.example.back.dto.MessageResponse;
import com.example.back.dto.RegisterRequest;
import com.example.back.domain.Role;
import com.example.back.domain.UserBean;
import com.example.back.repository.RoleRepository;
import com.example.back.repository.UserRepository;
import com.example.back.security.jwt.JwtUtils;
import com.example.back.service.UserDetailsImpl;

import jakarta.validation.Valid;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("普通用戶登入請求: {}", loginRequest.getUsername());
        
        // 先檢查用戶是否存在
        Optional<UserBean> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (!userOpt.isPresent()) {
            logger.warn("用戶不存在: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body(new MessageResponse("帳號或密碼錯誤"));
        }
        
        UserBean user = userOpt.get();
        
        // 檢查是否為管理員角色
        boolean isAdmin = user.getRoles().stream()
            .anyMatch(role -> 
                "ROLE_ADMIN".equals(role.getRoleName()) || 
                "ROLE_SUPER_ADMIN".equals(role.getRoleName()));
        
        if (isAdmin) {
            logger.warn("管理員帳號 {} 嘗試在普通登入頁面登入", loginRequest.getUsername());
            return ResponseEntity.status(403).body(new MessageResponse("管理員帳號請使用管理員登入頁面登入"));
        }
        
        // 使用try-catch捕獲認證異常
        try {
            // 進行認證
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // 再次檢查是否為管理員角色（以防萬一）
            boolean isAdminRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_ADMIN"));
                
            if (isAdminRole) {
                logger.warn("管理員帳號 {} 通過認證但被普通登入頁面拒絕", loginRequest.getUsername());
                return ResponseEntity.status(403).body(new MessageResponse("管理員帳號請使用管理員登入頁面登入"));
            }
            
            // 設置認證上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成JWT令牌
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);
            
            List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
            
            logger.info("用戶 {} 登入成功，角色: {}", userDetails.getUsername(), roles);
            
            return ResponseEntity.ok(new JwtResponse(
                jwt, 
                refreshToken,
                userDetails.getId(), 
                userDetails.getUsername(), 
                userDetails.getEmail(), 
                roles));
                
        } catch (BadCredentialsException e) {
            logger.error("認證失敗，密碼錯誤: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body(new MessageResponse("帳號或密碼錯誤"));
        } catch (Exception e) {
            logger.error("登入過程發生異常: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(500).body(new MessageResponse("登入過程中發生錯誤，請稍後再試"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // 添加除錯日誌
        logger.info("接收到註冊請求: {} - {}", registerRequest.getUsername(), registerRequest.getEmail());
        
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
        
        logger.info("用戶註冊成功: {}", registerRequest.getUsername());
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        logger.info("用戶登出");
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("登出成功，請重新登入"));
    }
}