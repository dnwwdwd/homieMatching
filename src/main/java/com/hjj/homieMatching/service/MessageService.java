package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.Message;

/**
 * 消息服务
 *
 */
public interface MessageService extends IService<Message> {

    /**
     * 获取消息编号
     *
     * @param userId 用户id
     * @return long
     */
    long getMessageNum(Long userId);


    /**
     * 有新消息
     *
     * @param userId 用户id
     * @return {@link Boolean}
     */
    Boolean hasNewMessage(Long userId);

}
