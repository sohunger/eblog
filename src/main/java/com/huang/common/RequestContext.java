package com.huang.common;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RequestContext {

    private String traceId;

    private String userId;

    private String clientType;

    public static RequestContext of(String traceId, String clientType) {
        RequestContext ctx = new RequestContext();
        ctx.setTraceId(traceId);
        ctx.setClientType(clientType);
        return ctx;
    }
}
