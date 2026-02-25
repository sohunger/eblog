package com.huang.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireCaptcha {

    /**
     * 验证码的uuid字段
     */
    String uuidField() default "uuid";

    /**
     * 验证码字段
     */
    String captchaField() default "captcha";
}
