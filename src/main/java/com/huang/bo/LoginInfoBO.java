package com.huang.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginInfoBO {

    /** 邮箱 */
    @NotBlank(message = "用户邮箱不能为空")
    private String email;

    /** 密码 */
    @NotBlank(message = "用户密码不能为空")
    private String password;

    /** 验证码 */
    @NotBlank(message = "验证码不能为空")
    private String captcha;
}
