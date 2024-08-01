package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageQueryRequest extends PageRequest implements Serializable {

    private Integer type; // 消息类型 - 0 - 收藏 1 - 点赞 2 - 关注消息 3 - 私发消息 4 - 队伍消息

}
