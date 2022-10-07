package com.huang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.entity.MUserMessage;
import com.huang.mapper.MUserMessageMapper;
import com.huang.service.MUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huang.vo.UserMessageVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MUserMessageServiceImpl extends ServiceImpl<MUserMessageMapper, MUserMessage> implements MUserMessageService {

    @Autowired
    MUserMessageMapper userMessageMapper;

    @Override
    public IPage<UserMessageVo> paging(Page page, QueryWrapper<MUserMessage> wrapper) {
        return userMessageMapper.userMessageResult(page, wrapper);

    }

    @Override
    public void updateToReaded(List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        userMessageMapper.updateToRead(new QueryWrapper<MUserMessage>()
                .in("id", ids)
        );
    }
}
