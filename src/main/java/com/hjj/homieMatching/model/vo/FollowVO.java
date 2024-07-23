package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FollowVO implements Serializable {

    private Long id;

    private String username;

    private String avatarUrl;

    private Boolean isFollowed;

    private static final long serialVersionUID = 1L;
}
