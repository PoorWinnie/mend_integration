package com.example.back.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.back.security.jwt.JwtUtils;
import com.example.back.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 寬鬆的認證過濾器 - 重點是確保公開路徑不需要認證
 */
public class PermissiveAuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(PermissiveAuthTokenFilter.class);
    
    // 明確列出不需要過濾的路徑
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register", 
        "/api/public/**"
    );
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();	

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        logger.debug("處理請求: {}", requestPath);
        
        // 為所有請求添加CORS頭 - 確保前端可以訪問
        addCorsHeaders(response);
        
        // 如果是OPTIONS請求，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 檢查是否是公開路徑
        if (isPublicPath(requestPath)) {
            logger.info("公開路徑，直接放行無需認證: {}", requestPath);
            // 清空認證狀態，確保匿名訪問
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        
        // 處理需要JWT驗證的路徑
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("JWT認證錯誤: {}", e.getMessage());
            // 發生錯誤時不要阻止請求繼續
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 檢查路徑是否是公開路徑
     */
    private boolean isPublicPath(String path) {
        // 檢查是否匹配任何公開路徑模式
        for (String publicPath : PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, path)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 添加CORS頭
     */
    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
    
    /**
     * 從請求中解析JWT令牌
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}