package com.example.back.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        
        String path = request.getServletPath();
        
        // 特別處理/error路徑
        if (path.equals("/error")) {
            logger.info("錯誤路徑，不返回額外的401錯誤: {}", path);
            return;
        }
        
        // 檢查是否為公開路徑
        if (isPublicPath(request)) {
            logger.info("公開路徑，允許訪問: {}", path);
            return;
        }
        
        logger.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", path);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
    
    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getServletPath();
        
        // 判斷是否為公開路徑
        return pathMatcher.match("/api/auth/login", path) ||
               pathMatcher.match("/api/auth/register", path) ||
               pathMatcher.match("/api/auth/admin/login", path) || // 新增：管理員登入路徑
               pathMatcher.match("/api/public/**", path) ||
               pathMatcher.match("/public/**", path) ||
               pathMatcher.match("/error", path) ||
               pathMatcher.match("/api-docs/**", path) ||
               pathMatcher.match("/swagger-ui/**", path) ||
               pathMatcher.match("/swagger-ui.html", path) ||
               "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}