package com.huang.controller;

import com.huang.common.Constant;
import com.huang.common.lang.Result;
import com.huang.entity.MPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
@Tag(name = "管理员", description = "管理员")
public class AdminController extends BaseController {

    @PostMapping("/set")
    @ResponseBody
    @Operation(summary = "设置帖子属性", description = "设置帖子属性")
    public Result adminSet(@Parameter(description = "帖子id") Long id
            , @Parameter(description = "等级") Integer rank
            , @Parameter(description = "操作内容") String field) {
        MPost post = postService.getById(id);
        Assert.notNull(post, "此帖子已经被删除");
        if (Constant.PostActionType.DELETE.equals(field)) {
            postService.removeById(id);
            return Result.success("删除成功");
        } else if (Constant.PostActionType.ESSENCE.equals(field)) {
            post.setRecommend(rank > 0);
        } else if (Constant.PostActionType.TOP.equals(field)) {
            post.setLevel(rank);
        }
        postService.updateById(post);
        return Result.success();
    }

}
