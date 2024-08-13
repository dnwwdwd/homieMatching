package com.hjj.homieMatching.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户性别
     */
    private Integer gender;
    /**
     * 用户简介
     */
    private String profile;
    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;
    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 维度
     */
    private Double dimension;

    /**
     * 博客数量
     */
    private Long blogNum;

    /**
     * 博客浏览量
     */
    private Long blogViewNum;

    /**
     * 关注数
     */
    private Long followNum;

    /**
     * 粉丝数
     */
    private Long fanNum;

    /**
     * 积分
     */
    private Integer score;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}