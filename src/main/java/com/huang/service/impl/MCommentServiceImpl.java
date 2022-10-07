package com.huang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MComment;
import com.huang.mapper.MCommentMapper;
import com.huang.service.MCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huang.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
@Service
public class MCommentServiceImpl extends ServiceImpl<MCommentMapper, MComment> implements MCommentService {

    @Autowired
    MCommentMapper commentMapper;

    @Override
    public IPage<CommentVo> paging(Page gtePage, Long userId, Long postId, String order) {
        return commentMapper.selectComments(gtePage, new QueryWrapper<MComment>()
                .eq(userId != null, "user_id", userId)
                .eq(postId != null, "post_id", postId)
                .orderByDesc(order != null, order)
        );
    }
}
