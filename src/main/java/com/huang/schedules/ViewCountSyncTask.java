package com.huang.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huang.entity.MPost;
import com.huang.service.MPostService;
import com.huang.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ViewCountSyncTask {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MPostService postService;

    @Autowired
    RedisTemplate redisTemplate;

    //五秒更新
    @Scheduled(cron = "0/5 * * * * *")
    public void task() {
        Set<String> keys = redisTemplate.keys("rank:post*");

        List<String> idList = new ArrayList();
        for (String key : keys) {
            if (redisUtil.hHasKey(key, "post:viewCount")) {
                idList.add(key.substring("rank:post".length()));
            }
        }
        if (idList.isEmpty()) {
            return;
        }
        List<MPost> posts = postService.list(new QueryWrapper<MPost>().in("id", idList));
        posts.forEach((post) -> {
            Integer viewCount = (Integer) redisUtil.hget("rank:post" + post.getId(), "post:viewCount");
            post.setViewCount(viewCount);
        });
        if (posts.isEmpty()) return;
        //同步到数据库中
        boolean isSuc = postService.updateBatchById(posts);

        //判断是否同步成功，同步成功则将缓存中的信息删除
        if (isSuc) {
            idList.forEach((id) -> {
                redisUtil.hdel("rank:post" + id, "post:viewCount");
                System.out.println(id + "-------------->同步成功");
            });
        }

    }
}
