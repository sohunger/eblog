package com.huang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    /**
     * 密码编码器。必须配置，用于加密验证密码。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 推荐使用强度更高的 BCrypt
        return new BCryptPasswordEncoder();
    }
}
