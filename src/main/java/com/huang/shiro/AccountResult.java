package com.huang.shiro;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountResult implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String sign;
    private String gender;
    private String avatar;

    public String getSex() {

        return "0".equals(gender) ? "女" : "男";
    }
}
