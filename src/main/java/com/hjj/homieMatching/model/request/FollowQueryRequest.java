package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class FollowQueryRequest extends PageRequest implements Serializable {

    private int type; // 查询类型（0 查自己粉丝，1 查自己关注，2 查别人粉丝，3 查别人关注）

    private long userId;
}
