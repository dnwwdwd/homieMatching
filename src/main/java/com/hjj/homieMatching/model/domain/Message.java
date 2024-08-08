package com.hjj.homieMatching.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型- 0 点赞 1-收藏 2- 关注消息 3 - 私发消息 4 - 队伍消息
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

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}