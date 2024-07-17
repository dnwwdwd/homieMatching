package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogUserVO implements Serializable {
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 粉丝数
     */
    private Integer fanNum;

    /**
     * 博客数
     */
    private Long blogNum;

    /**
     * 浏览量
     */
    private Integer viewNum;
}
