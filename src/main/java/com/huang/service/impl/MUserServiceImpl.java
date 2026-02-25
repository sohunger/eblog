package com.huang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huang.bo.RegisterInfoBO;
import com.huang.common.lang.Result;
import com.huang.entity.MUser;
import com.huang.mapper.MUserMapper;
import com.huang.service.MUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
@Service
@Slf4j
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser> implements MUserService {

    private final PasswordEncoder passwordEncoder;

    public MUserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 默认头像
     */
    private static final String DEFAULT_PNG = "/res/images/avatar/default.png";

    @Override
    public Result registerUser(RegisterInfoBO registerInfoBO) {
        log.info("用户请求注册信息{}", registerInfoBO);
        // 检查当前邮箱或用户名是否已经注册
        long count = this.count(new QueryWrapper<MUser>()
                .eq("userName", registerInfoBO.getUsername())
                .or()
                .eq("email", registerInfoBO.getEmail())
        );

        // 已占用则返回异常
        if (count > 0) {
            return Result.fail("用户名或邮箱已被占用");
        }

        // 检查两次输入的密码是否一致
        if (!registerInfoBO.getPassword().equals(registerInfoBO.getRePass())) {
            return Result.fail("两次输入的密码不一致");
        }

        //
        MUser muser = new MUser();
        //保证安全，重新创建一个对象，防止前端注入不需要的值。
        muser.setUsername(registerInfoBO.getUsername());
        muser.setEmail(registerInfoBO.getEmail());
        muser.setPassword(passwordEncoder.encode(registerInfoBO.getPassword()));
        muser.setPoint(0);
        muser.setPostCount(0);
        muser.setVipLevel(0);
        muser.setCreated(new Date());
        muser.setCommentCount(0);
        muser.setGender(registerInfoBO.getGender().name());
        muser.setAvatar(DEFAULT_PNG);
        muser.setRole("USER");
        muser.setLasted(new Date());

        this.save(muser);

        return Result.success();

    }

//    @Override
//    public AccountResult loginUser(String email, String password) {
//        MUser user = this.getOne(new QueryWrapper<MUser>().eq("email", email));
//        if (user == null) {
////            throw new UnknownAccountException();
//        }
//
//        if (!user.getPassword().equals(password)) {
////            throw new IncorrectCredentialsException();
//        }
//
//        user.setLasted(new Date());
//        this.updateById(user);
//
//        AccountResult result = new AccountResult();
//        BeanUtil.copyProperties(user, result);
//
//        return result;
//    }

}
