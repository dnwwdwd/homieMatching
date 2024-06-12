package com.hjj.homieMatching.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TeamUserVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     *  描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队伍创建者id
     */
    private Long userId;

    /**
     * 队伍状态 - 0 - 公开， 1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 入队用户列表
     */
    private List<UserVO> userList;
    /**
     * 创建人用户信息
     */
    private UserVO createUser;
    /**
     * 是否已进入某个队伍
     */
    private boolean hasJoin = false;
    /**
     * 已加入队伍的用户数
     */
    private Integer hasJoinNum;
}
