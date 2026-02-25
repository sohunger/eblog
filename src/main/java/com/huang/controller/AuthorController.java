package com.huang.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.StrUtil;
import com.huang.annotation.RequireCaptcha;
import com.huang.bo.LoginInfoBO;
import com.huang.bo.RegisterInfoBO;
import com.huang.common.RequestContext;
import com.huang.common.lang.Result;
import com.huang.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "认证管理", description = "认证管理")
@Slf4j
public class AuthorController extends BaseController {
    public static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    private final StringRedisTemplate stringRedisTemplate;

    public AuthorController(StringRedisTemplate stringRedisTemplate) {
        super();
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Operation(summary = "获取验证码", description = "获取验证码图片")
    @GetMapping("/kaptcha.jpg")
    public void kaptcha(HttpServletResponse resp, String uuid) throws IOException {
        // 获取验证码图形内容
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 5);

        // 获取图形验证码的数字内容
        String code = lineCaptcha.getCode();
        log.info("uuid {} 生成验证内容：[{}]", uuid, code);
        // 缓存验证码内容，缓存时间两分钟
        stringRedisTemplate.opsForValue().set(RedisKeyUtil.getCaptchaKey(uuid), code, Duration.ofMinutes(2));
        // 返回验证码内容
        lineCaptcha.write(resp.getOutputStream());
    }
    
//    @ResponseBody
//    @PostMapping("/login")
//    @Operation(summary = "用户登录", description = "用户登录验证")
//    public Result backLogin(@RequestBody @Valid LoginInfoBO loginInfoBO, RequestContext requestContext) {
//
//        return Result.success();
//    }

    
    @ResponseBody
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    @RequireCaptcha
    public Result BackRegister(@RequestBody @Valid RegisterInfoBO registerInfoBO, RequestContext requestContext) {

        return userService.registerUser(registerInfoBO);
    }
}
