package com.huang.common.interceptor;

import cn.hutool.json.JSONUtil;
import com.huang.common.RequestContext;
import com.huang.common.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

@Component
@Slf4j
public class RequestContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 获取请求公共数据
        String traceId = request.getHeader("x-trace-id");
        String clientType = request.getHeader("x-client-type");
        String userId = request.getHeader("x-user-id");

        // 创建context对象
        RequestContext requestContext = RequestContext.of(traceId, clientType);
        requestContext.setUserId(userId);

        // 放入到TreadLocal中
        RequestContextHolder.set(requestContext);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        // 请求结束必须清除掉ThreadLocal中的数据，否则会造成内存泄漏
        RequestContextHolder.clearContext();
    }


}
