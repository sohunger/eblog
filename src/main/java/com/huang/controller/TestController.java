package com.huang.controller;

import cn.hutool.json.JSONUtil;
import com.huang.bo.TestBO;
import com.huang.common.RepeatableReadRequestWrapper;
import com.huang.common.lang.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @PostMapping("/map")
    public String testMap(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        log.info("map [{}]", JSONUtil.toJsonStr(map));
//        printWrapperChain(request);
        return "Received: " + map.toString();
    }

    @PostMapping("/simpleObject")
    public String testSimpleObject(@RequestBody TestBO dto) {
        // 测试你的具体DTO
        return "Received userName: " + dto.getUsername();
    }

    @PostMapping("/wrapper")
    public String testWrapper(HttpServletRequest request) {
        // 尝试直接使用包装器的方法读取
        if (request instanceof RepeatableReadRequestWrapper) {
            try {
                String body = ((RepeatableReadRequestWrapper) request).getBodyString();
                return "Body from wrapper: \"" + (body != null ? body : "NULL") + "\"";
            } catch (Exception e) {
                return "Error reading wrapper body: " + e.getMessage();
            }
        }
        return "Request is NOT a wrapper, it's: " + request.getClass().getName();
    }

    @PostMapping("/simple")
    public String testSimple(@RequestBody String rawBody) { // 直接接收字符串
        return "Body via @RequestBody: \"" + rawBody + "\"";
    }

    public static void printWrapperChain(HttpServletRequest request) {
        System.out.println("Wrapper Chain:");
        HttpServletRequest current = request;
        int depth = 0;

        while (current != null) {
            System.out.println(depth + ": " + current.getClass().getName());

            if (current instanceof HttpServletRequestWrapper) {
                try {
                    Field requestField = HttpServletRequestWrapper.class
                            .getDeclaredField("request");
                    requestField.setAccessible(true);
                    current = (HttpServletRequest) requestField.get(current);
                    depth++;
                } catch (Exception e) {
                    break;
                }
            } else {
                break;
            }
        }
    }
}
