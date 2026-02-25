package com.huang.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.huang.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义JSON格式登录过滤器
 */
@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "email";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    public static final String CAPTCHA_UUID_KEY = "uuid";
    public static final String CAPTCHA_CODE_KEY = "captcha";

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
    private boolean postOnly = true;
    private final StringRedisTemplate stringRedisTemplate;


    public CustomAuthenticationFilter(StringRedisTemplate stringRedisTemplate) {
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login"));
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
            throw new AuthenticationServiceException("仅支持JSON格式登录请求");
        }

        Map<String, String> loginInfo;
        try {
            loginInfo = parseLoginInfoFromJson(request);
        } catch (Exception e) {
            // 捕获所有异常（包括JSON解析异常），包装为AuthenticationServiceException
            throw new AuthenticationServiceException("解析登录请求失败: " + e.getMessage(), e);
        }

        // 先进行验证码校验（如果传入）
        String uuid = loginInfo.get(CAPTCHA_UUID_KEY);
        String captcha = loginInfo.get(CAPTCHA_CODE_KEY);
        
        try {
            validateCaptchaIfPresent(uuid, captcha);
        } catch (AuthenticationException e) {
            throw e; // 直接抛出认证异常
        } catch (Exception e) {
            throw new AuthenticationServiceException("验证码校验失败: " + e.getMessage(), e);
        }

        String username = loginInfo.get("username");
        String password = loginInfo.get("password");

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();

        // 5. 创建认证令牌
        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        // 6. 设置详细信息
        setDetails(request, authRequest);

        // 7. 进行认证
        try {
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            // 防止 AuthenticationManager 为空或其他未检查异常导致过滤器链中断
            throw new AuthenticationServiceException("系统认证服务异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从JSON请求中解析登录信息
     */
    private Map<String, String> parseLoginInfoFromJson(HttpServletRequest request) throws IOException {
        HashMap<String, String> result = new HashMap<>();
        try {
            String requestBody = StreamUtils.copyToString(
                    request.getInputStream(), StandardCharsets.UTF_8);

            if (!requestBody.isEmpty()) {
                JSONObject jsonNode = JSONUtil.parseObj(requestBody);

                String username = "";
                String password = "";
                String uuid = "";
                String captcha = "";
                // 支持多种登录标识字段
                if (jsonNode.containsKey("username")) {
                    username = jsonNode.getStr("username");
                } else if (jsonNode.containsKey("email")) {
                    username = jsonNode.getStr("email");
                } else if (jsonNode.containsKey("phone")) {
                    username = jsonNode.getStr("phone");
                }

                if (jsonNode.containsKey("password")) {
                    password = jsonNode.getStr("password");
                }
                if (jsonNode.containsKey(CAPTCHA_UUID_KEY)) {
                    uuid = jsonNode.getStr(CAPTCHA_UUID_KEY);
                }
                if (jsonNode.containsKey(CAPTCHA_CODE_KEY)) {
                    captcha = jsonNode.getStr(CAPTCHA_CODE_KEY);
                }

                result.put("username", username);
                result.put("password", password);
                result.put(CAPTCHA_UUID_KEY, uuid);
                result.put(CAPTCHA_CODE_KEY, captcha);
            }
        } catch (Exception e) {
            // 捕获所有可能的解析异常
            throw new RuntimeException("JSON parsing failed", e);
        }
        return result;
    }

    /**
     * 如果请求中包含验证码字段，则进行校验
     */
    private void validateCaptchaIfPresent(String uuid, String captcha) {
        if (uuid == null && captcha == null) {
            return;
        }
        if (uuid == null || captcha == null) {
            throw new AuthenticationServiceException("验证码参数缺失");
        }
        String key = RedisKeyUtil.getCaptchaKey(uuid);
        String correct = stringRedisTemplate.opsForValue().get(key);
        if (correct == null) {
            throw new AuthenticationServiceException("验证码已过期");
        }
        if (!correct.equalsIgnoreCase(captcha)) {
            throw new AuthenticationServiceException("验证码错误");
        }
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取用户名
     */
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    /**
     * 获取密码
     */
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    /**
     * 设置详细信息
     */
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return usernameParameter;
    }

    public final String getPasswordParameter() {
        return passwordParameter;
    }
}
