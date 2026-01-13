package com.example.back.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.back.security.jwt.JwtAuthEntryPoint;
import com.example.back.security.jwt.JwtUtils;
import com.example.back.security.jwt.SimpleJwtFilter;
import com.example.back.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Bean
    public SimpleJwtFilter jwtAuthenticationFilter() {
        return new SimpleJwtFilter(jwtUtils, userDetailsService);
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        // 確保認證提供者總是拋出異常而不是返回null
        authProvider.setHideUserNotFoundExceptions(false);
        
        logger.info("配置認證提供者，使用BCryptPasswordEncoder");
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        logger.info("配置認證管理器");
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 修改: 允許所有常用前端開發端口，確保完全匹配前端地址
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174", 
            "http://localhost:8080",
            "http://localhost:3000",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:5174",
            "http://127.0.0.1:8080",
            "http://127.0.0.1:3000",
            "http://192.168.0.47:5173",
            "http://172.17.96.1:4173"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 修改: 明確列出允許的頭部，而不是使用通配符
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        // 關鍵設置: 啟用憑證支持，確保前端的withCredentials能正常工作
        configuration.setAllowCredentials(true);
        // 設置暴露的標頭
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // 設置最大緩存時間
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        logger.info("CORS配置已更新: 允許憑證並設置明確的來源");
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("配置安全過濾鏈");
        
        http.csrf(csrf -> csrf.disable());
        
        // 明確配置CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
        http.exceptionHandling(exception -> 
            exception.authenticationEntryPoint(unauthorizedHandler)
        );
        
        http.sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        
        // 配置公開路徑，明確指定精確的路徑
        http.authorizeHttpRequests(auth -> 
            auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/api/auth/admin/login").permitAll()
                .requestMatchers("/auth/login", "/auth/register").permitAll() // 添加額外的認證路徑
                .requestMatchers("/auth/admin/login").permitAll() // 添加額外的認證路徑
                .requestMatchers("/img/**").permitAll()
                .requestMatchers("/api/public/**").permitAll() //營區瀏覽
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/reviews/*").permitAll() //評價系統
                .requestMatchers("/static/uploads/**").permitAll()
                .requestMatchers("/uploads/**").permitAll() // 新增：直接訪問上傳目錄
                .requestMatchers("/ajax/products/**").permitAll()
                .requestMatchers("/ajax/product-types").permitAll() // 新增：產品類型API
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/ajax/products/**","/ajax/product-types/**").permitAll()
                .requestMatchers("/api/orders/**").permitAll()
                .anyRequest().authenticated()
        );
        
        // 確保認證提供者被正確注冊
        http.authenticationProvider(authenticationProvider());
        
        // 使用JWT過濾器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}