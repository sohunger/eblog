package com.huang.config;


import com.huang.security.CustomAuthenticationFilter;
import com.huang.security.CustomAuthenticationFailureHandler;
import com.huang.security.CustomAuthenticationSuccessHandler;
import com.huang.jwt.JwtAuthenticationFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
@Data
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    private final StringRedisTemplate stringRedisTemplate;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("开始配置 Spring Security");
        
        // 创建自定义认证过滤器
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(stringRedisTemplate);
        customAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        customAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        // 使用 AuthenticationConfiguration 获取 AuthenticationManager，避免 http.getSharedObject 可能为空的问题
        customAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF

                .authorizeHttpRequests(authorize -> authorize
                        // 1. 配置请求授权规则（按顺序匹配）
                        .requestMatchers( "/public/**", "/api/public/**", "/kaptcha.jpg", "/register", "/test/**", "/actuator/**", "/login", "/", "/index", "/post", "/category").permitAll() // 允许所有人访问
                        .requestMatchers("/res/**", "/static/**").permitAll() // 允许所有人访问静态资源
                        .requestMatchers("/user/**", "/message/**").hasRole("USER") // 需要 USER 角色
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 需要 ADMIN 角色
                        .anyRequest().authenticated() // 所有其他请求都需要认证
                )
                // 关键：完全禁用默认的表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                
                // 开启无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 添加自定义过滤器来处理JSON格式的登录请求
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加JWT过滤器，在认证过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, CustomAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
