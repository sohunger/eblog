package com.huang.vo;

import com.huang.entity.MComment;
import lombok.Data;

@Data
public class CommentVo extends MComment {

    private Long authorId;
    private String authorName;
    private String authorAvatar;
}
