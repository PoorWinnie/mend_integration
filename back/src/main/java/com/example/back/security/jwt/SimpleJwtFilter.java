package com.example.back.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.back.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SimpleJwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SimpleJwtFilter.class);
    
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 明確定義公開路徑，避免使用模糊匹配
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/admin/login",
        "/auth/login",
        "/auth/register",
        "/auth/admin/login",
        "/api/public/**",
        "/api/reviews/*",
        "/error",
        "/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/img/**",
        "/static/uploads/**",
        "/ajax/products/**"
    );
    
    public SimpleJwtFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        logger.debug("SimpleJwtFilter處理請求: {}", path);
        
        // 重要：完全移除自定義CORS處理，讓Spring Security的CORS配置生效
        // 不再調用addCorsHeaders方法
        
        // 處理CORS預檢請求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.debug("OPTIONS預檢請求，跳過認證: {}", path);
            // 僅設置狀態碼，但不添加任何CORS頭部
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 如果是公開路徑，直接放行
        if (isPublicPath(path)) {
            logger.debug("公開路徑，跳過認證: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("JWT有效，用戶: {}", username);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("無效的JWT或找不到JWT: {}", path);
                // 確保未認證請求不會繼續處理
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            logger.error("JWT認證錯誤: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        // 使用嚴格的路徑匹配
        for (String publicPath : PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, path)) {
                return true;
            }
        }
        return false;
    }
    
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }  
    // 完全移除addCorsHeaders方法
}