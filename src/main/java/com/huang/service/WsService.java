package com.huang.service;

import org.springframework.stereotype.Service;


public interface WsService {
    void notifyMsgToUser(Long toUserId);
}
