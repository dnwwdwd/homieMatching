package com.hjj.homieMatching.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 网络套接字签证官
 *
 * @author OchiaMalu
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "websocket返回")
public class WebSocketVO implements Serializable {

    private static final long serialVersionUID = 4696612253320170315L;

    @ApiModelProperty(value = "id")
    private long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String username;

    /**
     * 账号
     */
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

}
