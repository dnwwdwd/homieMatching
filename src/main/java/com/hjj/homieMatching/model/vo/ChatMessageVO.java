package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMessageVO implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = -4722378360550337925L;
    /**
     * 形式用户
     */
    private WebSocketVO fromUser;
    /**
     * 用户
     */
    private WebSocketVO toUser;
    /**
     * 团队id
     */
    private Long teamId;
    /**
     * 文本
     */
    private String text;
    /**
     * 是我
     */
    private Boolean isMy = false;
    /**
     * 聊天类型
     */
    private Integer chatType;
    /**
     * 是管理
     */
    private Boolean isAdmin = false;
    /**
     * 创建时间
     */
    private String createTime;
}

