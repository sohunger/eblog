package com.huang.config;

import cn.hutool.core.map.MapUtil;
import com.huang.shiro.AccountRealm;
import com.huang.shiro.AccountResult;
import com.huang.shiro.AuthFilter;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(accountRealm);

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置登录的url和登录成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
        // 配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        filterFactoryBean.setFilters(MapUtil.of("auth", authFilter()));

        Map<String, String> hashMap = new LinkedHashMap<>();

        hashMap.put("/**", "anon");

        hashMap.put("/user/home", "auth");
        hashMap.put("/user/set", "auth");

        hashMap.put("/collection/add/", "auth");
        hashMap.put("/collection/remove/", "auth");
        hashMap.put("/collection/find/", "auth");

        hashMap.put("/post/edit/", "auth");
        hashMap.put("/post/reply/", "auth");


        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;
    }

    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }
}
