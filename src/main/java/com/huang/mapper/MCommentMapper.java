package com.huang.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huang.vo.CommentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
@Component
public interface MCommentMapper extends BaseMapper<MComment> {

    IPage<CommentVo> selectComments(Page gtePage, @Param(Constants.WRAPPER) QueryWrapper<MComment> orderByDesc);
}
