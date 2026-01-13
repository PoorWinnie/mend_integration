package com.example.back.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 除錯過濾器，用於記錄請求和確認安全配置正常工作
 * 使用高優先級確保先於其他過濾器執行
 */
@Component
@Order(10)
public class DebugFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(DebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 記錄請求資訊
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        
        logger.info("收到請求: {} {}{}", method, uri, query);
        
        // 處理所有請求的CORS頭
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // 特別處理公開註冊路徑
        if (uri.contains("/public/register") || uri.contains("/auth/register")) {
            logger.info("正在處理註冊請求: {}", uri);
            
            // 如果是預檢請求
            if ("OPTIONS".equalsIgnoreCase(method)) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }
        
        // 繼續過濾鏈
        filterChain.doFilter(request, response);
        
        // 記錄回應狀態
        logger.info("請求完成: {} {} - 狀態: {}", method, uri, response.getStatus());
    }
}