package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudyTaskUserVO implements Serializable {

    private long id;

    private String username;

    private String avatarUrl;

    private String profile;

    private String tags;

    private Long fanNum;

    private Long blogNum;

    private boolean isFollowed;

    private static final long serialVersionUID = 1L;

    public void setIsFollowed(boolean followed) {
        isFollowed = followed;
    }
}
