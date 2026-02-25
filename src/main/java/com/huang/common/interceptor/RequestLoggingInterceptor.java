package com.huang.common.interceptor;

import cn.hutool.json.JSONUtil;
import com.huang.common.RepeatableReadRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 记录请求开始信息
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // 准备打印请求数据
        StringBuilder reqLog = new StringBuilder()
                .append("\n=== 请求开始 === [").append("]\n")
                .append("URI    : ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\n")
                .append("客户端IP: ").append(getClientIp(request)).append("\n")
                .append("Headers: ").append(getHeadersAsString(request)).append("\n");

        // 4. 可选：打印查询参数或简单请求体（需谨慎）
        // --- 核心修改：智能获取请求参数 ---
        StringBuilder paramsInfo = new StringBuilder();

        // 1. 先尝试获取URL查询参数和表单参数 (对所有请求都适用)
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            paramsInfo.append("Query/Form Params: ").append(paramMap);
        }

        // 2. 如果是 JSON 请求体，则读取并记录 Body
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            // 读取并记录Body内容（注意长度控制和敏感信息过滤）
            if (request instanceof RepeatableReadRequestWrapper) {
                String body = ((RepeatableReadRequestWrapper) request).getBodyString();
                if (body != null && !body.trim().isEmpty()) {
                    // 安全起见，可限制打印长度
                    String logBody = body.length() > 500 ? body.substring(0, 500) + "..." : body;
                    if (!paramsInfo.isEmpty()) paramsInfo.append(" | ");
                    paramsInfo.append("JSON Body: ").append(logBody);
                }
            }
        }

        // 3. 如果有获取到任何参数信息，则记录日志
        if (!paramsInfo.isEmpty()) {
            reqLog.append("参数: ").append(paramsInfo).append("\n");
        }
        // ... [记录其他信息并打印最终日志] ...
        log.info(reqLog.toString());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        // 1. 计算请求耗时
        Long startTime = (Long) request.getAttribute("startTime");
        long duration = startTime != null ? System.currentTimeMillis() - startTime : -1;
        // 2. 记录请求完成信息
        StringBuilder respLog = new StringBuilder()
                .append("\n=== 请求结束 === [").append("]\n")
                .append("状态码: ").append(response.getStatus()).append("\n")
                .append("耗时  : ").append(duration).append("ms\n");
        if (ex != null) {
            respLog.append("异常  : ").append(ex.getClass().getName()).append(" - ").append(ex.getMessage());
        }
        log.info(respLog.toString());
    }

    /**
     * 获取客户端IP
     * @author Frank
     * @param request
     * @return
     * @date 2026/2/4
     */
    private String getClientIp(HttpServletRequest request) {
        // 获取真实IP，处理代理情况
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取请求头数据字符串
     * @author Frank
     * @param request
     * @return
     * @date 2026/2/4
     */
    private String getHeadersAsString(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder("{");
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.append(name).append("=").append(request.getHeader(name));
            if (headerNames.hasMoreElements()) headers.append(", ");
        }
        return headers.append("}").toString();
    }
}
