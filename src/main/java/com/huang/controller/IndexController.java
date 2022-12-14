package com.huang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController {

    @RequestMapping({"", "/", "index"})
    public String index() {

        //1分页信息  2分类 3用户 4置顶 5精选 6排序
        IPage results = postService.paging(gtePage(), null, null, null, null, "created");
        req.setAttribute("pageData", results);
        req.setAttribute("CurrentCategoryId", 0);


        return "index";
    }
}
