package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageVO implements Serializable {

    /**
     * 博客标题
     */
    private String title;

    private Long id;

    /**
     * 类型- 0 点赞 1-点赞 2- 关注消息 3 - 私发消息 4 - 队伍消息
     */
    private Integer type;

    /**
     * 消息发送的用户id
     */
    private Long fromId;

    /**
     * 消息接收的用户id
     */
    private Long toId;

    /**
     * 消息内容
     */
    private String text;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 队伍 id
     */
    private Long teamId;

    /**
     * 博客 id
     */
    private Long blogId;

    /**
     * 已读-0 未读 ,1 已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private Date createTime;
}
