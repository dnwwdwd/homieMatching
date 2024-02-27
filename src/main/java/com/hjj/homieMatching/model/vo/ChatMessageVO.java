package com.hjj.homieMatching.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 聊天信息vo
 *
 * @author OchiaMalu
 * @date 2023/06/19
 */
@Data
@ApiModel(value = "聊天消息返回")
public class ChatMessageVO implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = -4722378360550337925L;
    /**
     * 形式用户
     */
    @ApiModelProperty(value = "发送id")
    private WebSocketVO fromUser;
    /**
     * 用户
     */
    @ApiModelProperty(value = "接收id")
    private WebSocketVO toUser;
    /**
     * 团队id
     */
    @ApiModelProperty(value = "队伍id")
    private Long teamId;
    /**
     * 文本
     */
    @ApiModelProperty(value = "正文")
    private String text;
    /**
     * 是我
     */
    @ApiModelProperty(value = "是否是我的消息")
    private Boolean isMy = false;
    /**
     * 聊天类型
     */
    @ApiModelProperty(value = "聊天类型")
    private Integer chatType;
    /**
     * 是管理
     */
    @ApiModelProperty(value = "是否为管理员")
    private Boolean isAdmin = false;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}

