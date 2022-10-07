package com.huang.vo;

import com.huang.entity.MUserMessage;
import lombok.Data;

@Data
public class UserMessageVo extends MUserMessage {

    private String fromUserName;
    private String toUserName;
    private String postTitle;
    //评论回复
    private String commentReply;


}
