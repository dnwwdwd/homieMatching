package com.hjj.homieMatching.model.vo;

import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

/**
 * 博客的作者（用户）VO
 */
@Data
@Setter
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
     * 标签
     */
    private String tags;
    /**
     * 粉丝数
     */
    private Long fanNum;

    /**
     * 博客数
     */
    private Long blogNum;

    /**
     * 浏览量
     */
    private Long blogViewNum;

    /**
     * 是否关注
     */
    private boolean isFollowed;

    public void setIsFollowed(boolean followed) {
        isFollowed = followed;
    }
}
