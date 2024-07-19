package com.hjj.homieMatching.model.vo;

import lombok.Data;

@Data
public class StarRequest {
    private boolean isStarred;

    private long blogId;

    public boolean isStarred() {
        return isStarred;
    }
}
