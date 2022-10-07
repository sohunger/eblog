package com.huang.shiro;

import com.huang.service.MUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    MUserService userService;

    //为id等于7的用户添加管理员权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        AccountResult result = (AccountResult) principalCollection.getPrimaryPrincipal();

        if (result.getId() == 7) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole("admin");
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;

        AccountResult result = userService.loginUser(usernamePasswordToken.getUsername(), String.valueOf(usernamePasswordToken.getPassword()));

        SecurityUtils.getSubject().getSession().setAttribute("result", result);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(result, authenticationToken.getCredentials(), getName());

        return info;

    }
}
