package com.hjj.homieMatching.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 聊天请求
 *
 */
@Data
@ApiModel(value = "用户队伍")
public class ChatRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1445805872513828206L;

    /**
     * 队伍聊天室id
     */
    @ApiModelProperty(value = "队伍聊天室id")
    private Long teamId;

    /**
     * 接收消息id
     */
    @ApiModelProperty(value = "接收消息id")
    private Long toId;

}