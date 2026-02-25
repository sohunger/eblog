package com.huang.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huang.entity.MUser;
import com.huang.service.MUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbUserDetailsService implements UserDetailsService {

    private final MUserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("开始检查用户[{}]权限=========", email);
        MUser user = userService.getOne(new QueryWrapper<MUser>().eq("email", email).or(wrapper -> wrapper.eq("username", email)));

        // 未查询到用户，抛出异常提示用户不存在
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户不存在 " + email);
        }

        // 获取用户权限信息
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority userRole = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        authorities.add(userRole);

        // 保存用户权限信息
        return User
                .withUsername(email)
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
