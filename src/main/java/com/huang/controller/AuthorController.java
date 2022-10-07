package com.huang.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.code.kaptcha.Producer;
import com.huang.common.lang.Result;
import com.huang.entity.MUser;
import com.huang.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Security;

@Controller
public class AuthorController extends BaseController {
    public static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";
    @Autowired
    Producer producer;

    @GetMapping("/kaptcha.jpg")
    public void kaptcha(HttpServletResponse resp) throws IOException {

        ServletOutputStream outputStream = resp.getOutputStream();

        String text = producer.createText();
        req.getSession().setAttribute("KAPTCHA_SESSION_KEY", text);
        BufferedImage image = producer.createImage(text);
        resp.setContentType("image/jpeg");
        resp.setHeader("Cache-Control", "no-store, no-cache");

        ImageIO.write(image, "jpg", outputStream);
    }

    @GetMapping("/login")
    public String login() {
        return "author/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result backLogin(String email, String password, String vercode) {
        if (StrUtil.isEmpty(email) || StrUtil.isBlank(password)) {
            return Result.fail("邮箱或密码不能为空");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));
        try {
            SecurityUtils.getSubject().login(token);

        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException) {
                return Result.fail("用户不存在");
            } else if (e instanceof LockedAccountException) {
                return Result.fail("用户被禁用");
            } else if (e instanceof IncorrectCredentialsException) {
                return Result.fail("密码错误");
            } else {
                return Result.fail("用户认证失败");
            }
        }
        return Result.success().action("/");
    }

    @ResponseBody
    @PostMapping("/register")
    public Result BackRegister(MUser user, String repass, String vercode) {
        String key = (String) req.getSession().getAttribute("KAPTCHA_SESSION_KEY");
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);

        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }
        if (!repass.equals(user.getPassword())) {
            return Result.fail("你的第二次密码输入错误!");
        }
        if (vercode == null || !vercode.equals(key)) {
            return Result.fail("验证码错误，请重试!");
        }

        Result result = userService.registerUser(user);
        return result.action("/login");
    }

    @GetMapping("/register")
    public String register() {
        return "author/reg";
    }

    @RequestMapping("/user/logout")
    public String logout() {

        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }
}
