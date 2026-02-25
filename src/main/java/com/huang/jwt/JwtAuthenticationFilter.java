package com.huang.jwt;

import com.huang.security.DbUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final DbUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, @Lazy DbUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1. 获取token
        String token = resolveToken(request);

        // 2. 检查token是否存在且SecurityContext中没有认证信息
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 3. 验证token格式和签名
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.extractUsername(token);
                
                if (username != null) {
                    // 4. 加载用户信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 5. 验证token是否对该用户有效（主要是检查是否过期）
                    if (jwtTokenProvider.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, 
                                        null, 
                                        userDetails.getAuthorities()
                                );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 6. 存入SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("JWT 认证成功: {}", username);
                    }
                }
            }
        }
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
