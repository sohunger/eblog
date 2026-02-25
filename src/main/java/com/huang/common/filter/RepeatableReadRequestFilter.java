package com.huang.common.filter;

import com.huang.common.RepeatableReadRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
// 将包装类过滤器设置到spring security过滤器(-100)后面执行，保证后面取到的request是自定义RepeatableReadRequestWrapper
@Order(0)
public class RepeatableReadRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, IOException {

        // 如果是需要包装的请求类型（如POST、PUT等）
            // 包装请求
        RepeatableReadRequestWrapper wrappedRequest =
                    new RepeatableReadRequestWrapper(request);
            // 继续过滤器链
            filterChain.doFilter(wrappedRequest, response);
    }

}
