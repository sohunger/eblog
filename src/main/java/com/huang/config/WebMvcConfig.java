package com.huang.config;


import com.huang.common.interceptor.CaptchaValidationInterceptor;
import com.huang.common.interceptor.RequestContextInterceptor;
import com.huang.common.interceptor.RequestLoggingInterceptor;
import com.huang.common.resolver.RequestContextArgumentResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Data
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringRedisTemplate stringRedisTemplate;

    private final RequestContextInterceptor requestContextInterceptor;
    private final CaptchaValidationInterceptor captchaValidationInterceptor;
    private final RequestContextArgumentResolver requestContextArgumentResolver;
    private final RequestLoggingInterceptor requestLoggingInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 请求数据打印拦截器
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**");

        // 请求上下文拦截器
        registry.addInterceptor(requestContextInterceptor)
                .addPathPatterns("/**"); // 拦截所有请求

        // 验证码拦截器
        registry.addInterceptor(captchaValidationInterceptor)
                .addPathPatterns("/login", "/register");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        // 注册参数解析器
        resolvers.add(requestContextArgumentResolver);
    }
}
