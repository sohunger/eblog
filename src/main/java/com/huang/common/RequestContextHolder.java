package com.huang.common;

public class RequestContextHolder {

    // 构建一个存储请求公共参数的上下文存储器
    private static final ThreadLocal<RequestContext> REQUEST_CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(RequestContext requestContext) {
        REQUEST_CONTEXT_HOLDER.set(requestContext);
    }

    public static RequestContext get() {
        return REQUEST_CONTEXT_HOLDER.get();
    }

    public static void clearContext() {
        REQUEST_CONTEXT_HOLDER.remove();
    }
}
