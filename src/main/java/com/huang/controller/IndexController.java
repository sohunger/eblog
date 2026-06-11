package com.huang.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.common.lang.Result;
import com.huang.entity.MCategory;
import com.huang.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "首页", description = "首页相关接口")
public class IndexController extends BaseController {

    
    @Operation(summary = "首页", description = "显示网站首页，支持分页参数 pn (当前页) 和 size (每页大小)，可选categoryId参数筛选分类")
    @RequestMapping({"", "/", "index"})
    public Result index(@RequestParam(required = false) Long categoryId) {

        IPage results = postService.paging(getPage(), categoryId, null, null, null, "created");
        List<MCategory> categories = categoryService.list();

        return Result.success(Map.of(
            "postList", results,
            "categories", categories
        ));
    }
}
