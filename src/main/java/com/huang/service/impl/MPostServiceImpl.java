package com.huang.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MPost;
import com.huang.mapper.MPostMapper;
import com.huang.service.MPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huang.util.RedisUtil;
import com.huang.vo.PostVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
@Service
public class MPostServiceImpl extends ServiceImpl<MPostMapper, MPost> implements MPostService {

    @Autowired
    MPostMapper postMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {
        if (level == null) {
            level = -1;
        }
        QueryWrapper wrapper = new QueryWrapper<MPost>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);
        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<MPost> wrapper) {
        return postMapper.selectOnePost(wrapper);
    }

    /**
     * 初始化每周热议信息
     */
    @Override
    public void initWeekRank() {

        //获取七天发表的文章
        List<MPost> posts = this.list(new QueryWrapper<MPost>()
                .ge("created", DateUtil.offsetDay(new Date(), -7))
                .select("id,title,user_id,comment_count,view_count,created")
        );
        System.out.println("初始化成功....................................................");
        //初始化文章的总评论数
        for (MPost post : posts) {
            String key = "day:rank" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);
            //参数1键，参数二值，参数三排序权重（本项目中存储的是评论发表的时间）
            redisUtil.zSet(key, post.getId(), post.getCommentCount());
            //设置七天自动过期，首先算出从创建时间到当前时间的间隔。
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            //然后用7减去间隔时间
            long expireTime = (7 - between) * 24 * 60 * 60;
            redisUtil.expire(key, expireTime);
            this.hashCachePostIdAndTitle(post, expireTime);
        }
        //并集
        this.zUnionAndStoreLast7DayForWeekRank();

    }

    @Override
    public void incrCommentCountAndUnionForWeekRank(Long postId, boolean isIncr) {
        String currentKey = "day:rank" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey, postId, isIncr ? 1 : -1);


        this.zUnionAndStoreLast7DayForWeekRank();
    }

    /**
     * 增加阅读量
     *
     * @param vo
     */
    @Override
    public void putViewCount(PostVo vo) {
        String key = "rank:post" + vo.getId();
        //判断缓存中是否有记录，如果有就从缓存读出，如果没有就从数据库直接读取
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");
        if (viewCount != null) {
            vo.setViewCount(viewCount + 1);
        } else {
            vo.setViewCount(vo.getViewCount() + 1);
        }
        //同步到缓存中
        redisUtil.hset(key, "post:viewCount", vo.getViewCount());
    }


    /**
     * 本周合并每日评论缓存
     */
    private void zUnionAndStoreLast7DayForWeekRank() {
        String destKey = "week:rank";
        String currentKey = "day:rank" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        List<String> otherKeys = new ArrayList<String>();
        for (int i = -6; i <= 0; i++) {
            otherKeys.add("day:rank" +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT));
        }
        redisUtil.zUnionAndStore(currentKey, otherKeys, destKey);
    }

    /**
     * 缓存文章基本信息
     *
     * @param post
     * @param expireTime
     */
    private void hashCachePostIdAndTitle(MPost post, long expireTime) {
        String key = "rank:post" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if (!hasKey) {
            redisUtil.hset(key, "post:id", post.getId(), expireTime);
            redisUtil.hset(key, "post:title", post.getTitle(), expireTime);
            redisUtil.hset(key, "post:viewCount", 1, expireTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), expireTime);
        }
    }


}
