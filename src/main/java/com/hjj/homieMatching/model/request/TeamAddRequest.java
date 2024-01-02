package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddRequest implements Serializable {
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
     * 队伍密码
     */
    private String password;
}
