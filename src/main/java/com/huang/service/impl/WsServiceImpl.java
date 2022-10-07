package com.huang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huang.entity.MUserMessage;
import com.huang.service.MUserMessageService;
import com.huang.service.WsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsServiceImpl implements WsService {
    @Autowired
    MUserMessageService userMessageService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Async
    @Override
    public void notifyMsgToUser(Long toUserId) {
        int count = userMessageService.count(new QueryWrapper<MUserMessage>()
                .eq("to_user_id", toUserId)
                .eq("status", "0")
        );

        simpMessagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);

    }
}
