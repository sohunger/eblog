package com.huang.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MUserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huang.vo.UserMessageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huang
 * @since 2022-03-18
 */
public interface MUserMessageService extends IService<MUserMessage> {

    IPage<UserMessageVo> paging(Page page, QueryWrapper<MUserMessage> wrapper);

    void updateToReaded(List<Long> ids);
}
