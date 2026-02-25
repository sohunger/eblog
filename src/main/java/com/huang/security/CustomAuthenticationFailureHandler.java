package com.huang.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huang.common.lang.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义登录失败处理器，返回JSON格式响应
 */
@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        log.error("用户登录失败: {}", exception.getMessage());
        
        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        
        // 创建失败响应
        Result result = Result.fail("登录失败: " + exception.getMessage());
        
        // 将结果写入响应
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}