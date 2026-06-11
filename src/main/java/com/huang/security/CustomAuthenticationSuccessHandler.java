package com.huang.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huang.common.lang.Result;
import com.huang.entity.MUser;
import com.huang.jwt.JwtTokenProvider;
import com.huang.service.MUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义登录成功处理器，返回JSON格式响应
 */
@Component
@Slf4j
@AllArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtTokenProvider jwtTokenProvider;

    private final MUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        log.info("用户登录成功: {}", authentication.getName());

        // 构建返回数据，增加token信息
        // 从 authentication 中获取用户详情
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 生成 JWT
        String token = jwtTokenProvider.generateToken(userDetails);

        // 查询用户信息获取头像
        MUser user = userService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MUser>()
                .eq("username", userDetails.getUsername()));

        // 构建返回的 JSON 数据
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userName", userDetails.getUsername());
        if (user != null) {
            resultMap.put("avatar", user.getAvatar());
        }

        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        
        // 创建成功响应
        Result result = Result.success(resultMap);
        
        // 将结果写入响应
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}