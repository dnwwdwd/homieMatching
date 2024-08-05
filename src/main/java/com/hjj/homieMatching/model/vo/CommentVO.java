package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVO implements Serializable {

    private Long id;

    private Long userId;

    private Long blogId;

    private String text;

    private String username;

    private String userAvatarUrl;
}
