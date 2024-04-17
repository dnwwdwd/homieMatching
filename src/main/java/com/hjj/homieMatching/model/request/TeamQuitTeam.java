package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 */
@Data
public class TeamQuitTeam implements Serializable {
    /**
     * 队伍id
     */
    private Long teamId;
}
