package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

public class CommentVO implements Serializable {

    private Long id;

    private Long userId;

    private Long blogId;

    private String text;

    private String username;

    private String userAvatarUrl;

    private boolean isMyComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public boolean getIsMyComment() {
        return this.isMyComment;
    }

    public void setIsMyComment(boolean isMyComment) {
        this.isMyComment = isMyComment;
    }
}
