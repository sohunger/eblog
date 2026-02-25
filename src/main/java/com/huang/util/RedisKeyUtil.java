package com.huang.util;

public class RedisKeyUtil {


    /**
     * 获取验证码的redis key
     * @author Frank
     * @param uuid 随机码
     * @return  String
     * @date 2026/1/28
     */
    public static String getCaptchaKey(String uuid) {
        return "captcha:" + uuid;
    }
}
