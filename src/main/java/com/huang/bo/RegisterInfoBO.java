package com.huang.bo;

import com.huang.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegisterInfoBO {

    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String rePass;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @NotBlank(message = "注册时uuid不能为空")
    private String uuid;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotNull(message = "用户性别不能为空")
    private Gender gender;
}
