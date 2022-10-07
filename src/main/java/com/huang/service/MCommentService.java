package com.huang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huang.mapper.MCommentMapper;
import com.huang.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
public interface MCommentService extends IService<MComment> {


    IPage<CommentVo> paging(Page gtePage, Long userId, Long postId, String created);
}
