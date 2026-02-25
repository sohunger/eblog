package com.huang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "首页", description = "首页相关接口")
public class IndexController extends BaseController {

    
    @Operation(summary = "首页", description = "显示网站首页")
    @RequestMapping({"", "/", "index"})
    public String index() {

        //1分页信息  2分类 3用户 4置顶 5精选 6排序
        IPage results = postService.paging(gtePage(), null, null, null, null, "created");
        req.setAttribute("pageData", results);
        req.setAttribute("CurrentCategoryId", 0);


        return "index";
    }
}
