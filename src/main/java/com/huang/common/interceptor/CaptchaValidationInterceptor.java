package com.huang.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.huang.annotation.RequireCaptcha;
import com.huang.common.RepeatableReadRequestWrapper;
import com.huang.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;


@Component
@Slf4j
@Data
public class CaptchaValidationInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 1.检查是否已经映射到了方法上的拦截器，如果不是则不进行处理
        // spring MVC会在请求映射到一个控制器的方法时，将这个方法包装成一个HandlerMethod对象
        // 如果不进行判断可能会有其他的类型的handler，例如获取资源的ResourceHttpRequestHandler，此时会出ClassCastException
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 2.拿到方法内容
        Method method = handlerMethod.getMethod();

        // 3.检查该方法上是否有验证码注解
        RequireCaptcha annotation = method.getAnnotation(RequireCaptcha.class);
        // 方法上没有验证码注解，检查方法所在的类上有没有注解
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequireCaptcha.class);
        }
        // 类上也没有注解，通过
        if (annotation == null) {
            return true;
        }

        // 4. 获取请求体
        RepeatableReadRequestWrapper wrapperRequest = (RepeatableReadRequestWrapper) request;
        String bodyString = wrapperRequest.getBodyString();

        // 5.获取request中的uuid和captcha
        if (StrUtil.isBlank(bodyString)) {
            sendErrorResponse(response, "请求体不能为空");
            return false;
        }
        JSONObject bodyObject = JSONUtil.parseObj(bodyString);
        String uuid = bodyObject.getStr("uuid");
        String captcha = bodyObject.getStr("captcha");

        // 6. 验证码校验
        String validateCaptchaResult = this.validateCaptcha(uuid, captcha);

        // 校验未通过
        if (StrUtil.isNotBlank(validateCaptchaResult)) {
            sendErrorResponse(response, validateCaptchaResult);
            return false;
        }

        // 7. 校验通过！将包装后的请求放回属性中，供后续Controller使用
        request.setAttribute("REPEATABLE_READ_REQUEST", wrapperRequest);
        return true;

    }

    /**
     * 检查验证码是否正确
     * @author Frank
     * @param uuid
     * @param userInputCaptcha
     * @return
     * @date 2026/1/29
     */
    private String validateCaptcha(String uuid, String userInputCaptcha) {
        if (StrUtil.isBlank(userInputCaptcha) || StrUtil.isBlank(uuid)) {
            log.error("未传入验证码内容 captcha = [{}], uuid = [{}]", userInputCaptcha, uuid);
            return "验证码参数缺失";
        }
        String redisKey = RedisKeyUtil.getCaptchaKey(uuid);
        String correctCaptcha = redisTemplate.opsForValue().get(redisKey);
        if (correctCaptcha == null) {
            log.error("验证码已过期");
            return "验证码已过期";
        }
        if (!correctCaptcha.equalsIgnoreCase(userInputCaptcha)) {
            log.error("验证码错误");
            return "验证码错误";
        }

        // 完成验证则删除验证码缓存
        redisTemplate.delete(redisKey);
        return null; // 返回null表示校验成功
    }

    /**
     * 返回异常内容工具方法
     * @author Frank
     * @param response 响应内容
     * @param message 异常描述
     * @throws IOException
     * @date 2026/1/28
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = JSONUtil.toJsonStr(
                java.util.Map.of("code", 400, "success", false, "message", message)
        );
        response.getWriter().write(jsonResponse);
    }
}
