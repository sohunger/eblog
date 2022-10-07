package com.huang.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huang.common.lang.Result;
import com.huang.entity.*;
import com.huang.util.ValidationUtil;
import com.huang.vo.CommentVo;
import com.huang.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class PostController extends BaseController {
    @GetMapping("post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id) {
        PostVo vo = postService.selectOnePost(new QueryWrapper<MPost>().eq("p.id", id));
        //1分页信息 2用户id 3文章id 4排序
        IPage<CommentVo> results = commentService.paging(gtePage(), null, vo.getId(), "created");
        //增加阅读量
        postService.putViewCount(vo);
        Assert.notNull(vo, "文章已被删除");
        req.setAttribute("post", vo);
        req.setAttribute("CurrentCategoryId", vo.getCategoryId());
        req.setAttribute("pageData", results);


        return "post/detail";
    }

    @GetMapping("category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id) {
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);


        req.setAttribute("CurrentCategoryId", id);
        req.setAttribute("pn", pn);
        return "post/category";
    }

    @ResponseBody
    @PostMapping("collection/find")
    public Result collectionFind(Long pid) {
        int count = userCollectionService.count(new QueryWrapper<MUserCollection>()
                .eq("user_id", getResultId())
                .eq("post_id", pid)
        );

        return Result.success(MapUtil.of("collection", count > 0));
    }

    @ResponseBody
    @PostMapping("collection/add/")
    public Result collectionAdd(Long pid) {

        MPost post = postService.getById(pid);

        Assert.isTrue(post != null, "改帖子已被删除");
        int count = userCollectionService.count(new QueryWrapper<MUserCollection>()
                .eq("user_id", getResultId())
                .eq("post_id", pid)
        );
        if (count > 0) {
            return Result.fail("该帖子已经被收藏");
        }
        MUserCollection collection = new MUserCollection();
        collection.setPostId(pid);
        collection.setUserId(getResultId());
        collection.setPostUserId(post.getUserId());
        collection.setCreated(new Date());
        collection.setModified(new Date());
        userCollectionService.save(collection);

        return Result.success();
    }


    @ResponseBody
    @PostMapping("collection/remove/")
    public Result collectionRemove(Long pid) {

        userCollectionService.remove(new QueryWrapper<MUserCollection>()
                .eq("user_id", getResultId())
                .eq("post_id", pid)
        );

        return Result.success();
    }

    @GetMapping("/post/edit")
    public String edit() {
        String id = req.getParameter("id");
        if (!StrUtil.isEmpty(id)) {
            MPost post = postService.getById(id);
            //Assert.isTrue如果为真，继续执行，如果为假，报出异常，一场信息为后面的message
            Assert.isTrue(post != null, "改帖子已被删除");
            Assert.isTrue(post.getUserId().longValue() == getResultId().longValue(), "没权限操作此文章");
            req.setAttribute("post", post);
        }
        req.setAttribute("categories", categoryService.list());

        return "post/edit";
    }

    @PostMapping("/post/submit")
    @ResponseBody
    public Result submit(MPost post) {
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        if (post.getId() == null) {
            post.setUserId(getResultId());

            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            postService.save(post);
        } else {
            MPost temp = postService.getById(post.getId());
            Assert.isTrue(temp.getUserId().longValue() == getResultId().longValue(), "无权限编辑此文章");

            temp.setTitle(post.getTitle());
            temp.setCategoryId(post.getCategoryId());
            temp.setContent(post.getContent());
            postService.updateById(temp);
        }

        return Result.success().action("/post/" + post.getId());
    }

    @PostMapping("/post/delete")
    @ResponseBody
    public Result delete(Long id) {
        MPost post = postService.getById(id);

        Assert.notNull(post, "该贴已经被删除");
        Assert.isTrue(getResultId().equals(post.getUserId()), "没有权限操作");

        postService.removeById(id);

        userMessageService.removeByMap(MapUtil.of("post_id", id));
        userCollectionService.removeByMap(MapUtil.of("post_id", id));
        return Result.success("删除成功");
    }

    @PostMapping("/post/reply")
    @ResponseBody
    public Result reply(String content, Long pid) {
        Assert.hasLength(content, "评论内容不能为空");
        Assert.notNull(pid, "找不到要评论的帖子");
        MPost post = postService.getById(pid);
        Assert.notNull(post, "此贴已经被删除");
        MComment com = new MComment();
        com.setPostId(pid);
        com.setContent(content);
        com.setUserId(getResultId());
        com.setCreated(new Date());
        com.setModified(new Date());
        com.setLevel(0);
        com.setVoteDown(0);
        com.setVoteUp(0);

        //将此贴的评论数增加
        post.setCommentCount(post.getCommentCount() + 1);
        //更新本周热议的评论数量
        postService.incrCommentCountAndUnionForWeekRank(pid, true);

        //对被评论的贴主进行通知,如果评论人是贴主不进行通知
        if (com.getUserId() != post.getUserId()) {
            MUserMessage msg = new MUserMessage();
            msg.setCommentId(com.getId());
            msg.setContent(com.getContent());
            msg.setFromUserId(com.getUserId());
            msg.setToUserId(post.getUserId());
            msg.setType(1);
            msg.setStatus(0);
            msg.setCreated(new Date());
            msg.setPostId(post.getId());
            msg.setModified(new Date());
            wsService.notifyMsgToUser(msg.getToUserId());
        }
        //评论的回复
        if (content.startsWith("@")) {
            String username = content.substring(1, content.indexOf(" "));
            MUser user = userService.getOne(new QueryWrapper<MUser>()
                    .eq("username", username)
            );
            if (user != null) {
                MUserMessage msg = new MUserMessage();
                msg.setCommentId(com.getId());
                msg.setContent(com.getContent());
                msg.setFromUserId(com.getUserId());
                msg.setToUserId(user.getId());
                msg.setType(2);
                msg.setStatus(0);
                msg.setCreated(new Date());
                msg.setPostId(post.getId());
                msg.setModified(new Date());
                com.setParentId(user.getId());
                userMessageService.save(msg);
            }
        }
        commentService.save(com);
        return Result.success().action("/post/" + pid);
    }


}
