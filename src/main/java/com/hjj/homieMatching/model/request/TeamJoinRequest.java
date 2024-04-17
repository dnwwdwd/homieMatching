package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求体
 */
@Data
public class TeamJoinRequest implements Serializable {
    /**
     * 密码
     */
    private String password;
    /**
     * 队伍的id
     */
    private Long teamId;
}
