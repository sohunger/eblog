package com.huang.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MUserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huang.vo.UserMessageVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
@Component
public interface MUserMessageMapper extends BaseMapper<MUserMessage> {


    IPage<UserMessageVo> userMessageResult(Page page, @Param(Constants.WRAPPER) QueryWrapper<MUserMessage> wrapper);

    @Transactional
    @Update("update m_user_message set status = 1 ${ew.customSqlSegment}")
    void updateToRead(@Param(Constants.WRAPPER) QueryWrapper<MUserMessage> id);
}
