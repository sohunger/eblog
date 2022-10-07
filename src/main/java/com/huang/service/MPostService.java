package com.huang.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huang.vo.PostVo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
public interface MPostService extends IService<MPost> {

    IPage paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVo selectOnePost(QueryWrapper<MPost> wrapper);

    void initWeekRank();

    void incrCommentCountAndUnionForWeekRank(Long postId, boolean isIncr);

    void putViewCount(PostVo vo);

}
