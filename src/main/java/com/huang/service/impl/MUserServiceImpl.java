package com.huang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huang.common.lang.Result;
import com.huang.entity.MUser;
import com.huang.mapper.MUserMapper;
import com.huang.service.MUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huang.shiro.AccountResult;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
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
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser> implements MUserService {

    @Override
    public Result registerUser(MUser user) {

        int count = this.count(new QueryWrapper<MUser>()
                .eq("userName", user.getUsername())
                .or()
                .eq("email", user.getEmail())
        );

        if (count > 0) {
            Result.fail("用户名或邮箱已被占用");
        }
        MUser muser = new MUser();
        //保证安全，重新创建一个对象，防止前端注入不需要的值。
        muser.setUsername(user.getUsername());
        muser.setEmail(user.getEmail());
        muser.setPassword(SecureUtil.md5(user.getPassword()));
        muser.setPoint(0);
        muser.setPostCount(0);
        muser.setVipLevel(0);
        muser.setCreated(new Date());
        muser.setCommentCount(0);
        muser.setGender("0");
        muser.setAvatar("/res/images/avatar/default.png");

        this.save(muser);

        return Result.success();

    }

    @Override
    public AccountResult loginUser(String email, String password) {
        MUser user = this.getOne(new QueryWrapper<MUser>().eq("email", email));
        if (user == null) {
            throw new UnknownAccountException();
        }

        if (!user.getPassword().equals(password)) {
            throw new IncorrectCredentialsException();
        }

        user.setLasted(new Date());
        this.updateById(user);

        AccountResult result = new AccountResult();
        BeanUtil.copyProperties(user, result);

        return result;
    }

}
