package com.hjj.homieMatching.model.vo;

import lombok.Data;

@Data
public class FollowRequest {

    private boolean isFollowed;

    private long blogId;

    public boolean isFollowed() {
        return isFollowed;
    }
}
