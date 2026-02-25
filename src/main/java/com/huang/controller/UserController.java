package com.huang.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huang.common.lang.Result;
import com.huang.entity.MPost;
import com.huang.entity.MUser;
import com.huang.entity.MUserMessage;
import com.huang.shiro.AccountResult;
import com.huang.vo.UserMessageVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "用户", description = "用户相关接口")
public class UserController extends BaseController {


    
    @Operation(summary = "用户主页", description = "显示用户主页信息")
    @GetMapping("/user/home")
    public String home() {

        MUser user = userService.getById(getResultId());
        List<MPost> posts = postService.list(new QueryWrapper<MPost>()
                .eq("user_id", getResultId())
                .orderByDesc("created")
        );

        req.setAttribute("posts", posts);
        req.setAttribute("user", user);
        return "/user/home";
    }

    
    @Operation(summary = "用户设置页面", description = "显示用户设置页面")
    @GetMapping("/user/set")
    public String set() {
        MUser user = userService.getById(getResultId());

        req.setAttribute("user", user);

        return "/user/set";
    }

    
    @ResponseBody
    @PostMapping("/user/set")
    @Operation(summary = "更新用户设置", description = "保存用户设置信息")
    public Result doSet(MUser user) {

        System.out.println(user.getAvatar());
        if (StrUtil.isNotBlank(user.getAvatar())) {

            MUser temp = userService.getById(getResultId());
            temp.setAvatar(user.getAvatar());
            userService.updateById(temp);

            AccountResult result = getResult();
            result.setAvatar(user.getAvatar());

//            SecurityUtils.getSubject().getSession().setAttribute("result", result);

            return Result.success().action("/user/set#avatar");
        }
        if (StrUtil.isBlank(user.getUsername())) {
            Result.fail("昵称不能为空");
        }
        long count = userService.count(new QueryWrapper<MUser>()
                .eq("username", user.getUsername())
                .ne("id", getResultId())
        );
        if (count > 0) {
            return Result.fail("当前昵称已经被占用");
        }

        MUser temp = userService.getById(getResultId());

        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        AccountResult result = getResult();
        result.setUsername(user.getUsername());
        result.setSign(user.getSign());
        result.setGender(user.getGender());
//        SecurityUtils.getSubject().getSession().setAttribute("result", result);

        return Result.success().action("/user/set#info");
    }

    
    @ResponseBody
    @PostMapping(value = "/user/upload")
    @Operation(summary = "上传头像", description = "上传用户头像")
    public Result uploadImg(@RequestParam(value = "file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return Result.fail("文件为空，请重新上传");
        }
        File myFile = multipartFileToFileUtil.multipartFileToFile(file);
        String path = myFile.getPath();
        String filename = file.getOriginalFilename();

        return qiniuCloudUtil.upload(path, filename, myFile);

    }

    
    @ResponseBody
    @PostMapping("/user/repass")
    @Operation(summary = "更新密码", description = "更新用户密码")
    public Result updatePass(String nowpass, String pass, String repass) {
        MUser user = userService.getById(getResultId());

        if (StrUtil.isBlank(pass)) {
            Result.fail("更改的密码不能为空");
        }
        if (!pass.equals(repass)) {
            Result.fail("两次输入的密码不一致");
        }
        String md5Pass = SecureUtil.md5(nowpass);
        if (!user.getPassword().equals(md5Pass)) {
            Result.fail("请输入正确的旧密码");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        return Result.success().action("/user/set#pass");

    }

    
    @Operation(summary = "用户消息", description = "显示用户消息列表")
    @GetMapping("/user/message")
    public String message() {
        IPage<UserMessageVo> page = userMessageService.paging(gtePage(), new QueryWrapper<MUserMessage>()
                .eq("to_user_id", getResultId())
                .orderByDesc("created")
        );

        List<Long> ids = new ArrayList<>();
        for (UserMessageVo messageVo : page.getRecords()) {
            if (messageVo.getStatus() == 0) {
                ids.add(messageVo.getId());
            }
        }
        // 批量修改成已读
        userMessageService.updateToReaded(ids);

        req.setAttribute("pageData", page);

        return "user/message";
    }

    
    @PostMapping("message/nums/")
    @ResponseBody
    @Operation(summary = "消息数量", description = "获取未读消息数量")
    public Map messageNums() {
        long count = userMessageService.count(new QueryWrapper<MUserMessage>()
                .eq("to_user_id", getResultId())
                .eq("status", "0")
        );
        return MapUtil.builder("status", 0)
                .put("count", (int) count).build();
    }

    
    @PostMapping("message/remove")
    @ResponseBody
    @Operation(summary = "删除消息", description = "删除指定消息")
    public Result messageRemove(Long id,
                                @RequestParam(defaultValue = "false") Boolean all) {

        boolean remove = userMessageService.remove(new QueryWrapper<MUserMessage>()
                .eq("to_user_id", getResultId())
                .eq(!all, "id", id)
        );

        return remove ? Result.success() : Result.fail("删除失败!");


    }

    
    @Operation(summary = "用户中心", description = "用户中心页面")
    @GetMapping("user/index")
    public String index() {
        return "user/index";
    }


    
    @ResponseBody
    @GetMapping("user/public")
    @Operation(summary = "用户发布的帖子", description = "获取用户发布的帖子列表")
    public Result userP() {
        IPage page = postService.page(gtePage(), new QueryWrapper<MPost>()
                .eq("user_id", getResultId())
                .orderByDesc("created")
        );
        return Result.success(page);
    }

    
    @ResponseBody
    @GetMapping("user/collection")
    @Operation(summary = "用户收藏的帖子", description = "获取用户收藏的帖子列表")
    public Result collection() {
        IPage page = postService.page(gtePage(), new QueryWrapper<MPost>()
                .inSql("id", "SELECT post_id FROM m_user_collection WHERE user_id=" + getResultId())
        );
        return Result.success(page);
    }


}
