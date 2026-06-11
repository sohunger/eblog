package com.huang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MUser;
import com.huang.entity.MUserMessage;
import com.huang.service.*;
import com.huang.util.MultipartFileToFileUtil;
import com.huang.util.QiniuCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.ServletRequestUtils;

import jakarta.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "基础控制器", description = "控制器基类")
public class BaseController {
    @Autowired
    HttpServletRequest req;
    @Autowired
    MPostService postService;
    @Autowired
    MCommentService commentService;
    @Autowired
    MUserService userService;
    @Autowired
    QiniuCloudUtil qiniuCloudUtil;
    @Autowired
    MultipartFileToFileUtil multipartFileToFileUtil;
    @Autowired
    MUserMessageService userMessageService;
    @Autowired
    MUserCollectionService userCollectionService;
    @Autowired
    MCategoryService categoryService;
    @Autowired
    WsService wsService;


    public Page getPage() {
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 10);
        return new Page(pn, size);
    }

    protected MUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.getOne(new QueryWrapper<MUser>()
                .eq("username", userDetails.getUsername())
                .or(wrapper -> wrapper.eq("email", userDetails.getUsername())));
    }

    protected Long getResultId() {
        MUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
