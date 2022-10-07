package com.huang.controller;

import com.huang.common.lang.Result;
import com.huang.entity.MPost;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @PostMapping("/set")
    @ResponseBody
    public Result adminSet(Long id, Integer rank, String field) {
        MPost post = postService.getById(id);
        Assert.notNull(post, "此帖子已经被删除");
        if ("delete".equals(field)) {
            postService.removeById(id);
            return Result.success("删除成功");
        } else if ("status".equals(field)) {
            post.setRecommend(rank > 0);
        } else if ("stick".equals(field)) {
            post.setLevel(rank);
        }
        postService.updateById(post);
        return Result.success();
    }

}
