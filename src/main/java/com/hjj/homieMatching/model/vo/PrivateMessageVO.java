package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PrivateMessageVO {
    private Long id;
    private Long friendId;
    private String avatarUrl;
    private String text;
    private Date createTime;
    private String username;
}
