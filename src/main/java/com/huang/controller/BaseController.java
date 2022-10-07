package com.huang.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MUserMessage;
import com.huang.service.*;
import com.huang.shiro.AccountRealm;
import com.huang.shiro.AccountResult;
import com.huang.util.MultipartFileToFileUtil;
import com.huang.util.QiniuCloudUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

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


    public Page gtePage() {
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 2);
        return new Page(pn, size);
    }

    protected AccountResult getResult() {
        return (AccountResult) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getResultId() {
        return getResult().getId();
    }
}
