package com.hjj.homieMatching.controller;


import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 发送消息控制器
 */
@RestController
public class MessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 消息发送地址
    private static final String BROADCAST_MESSAGE_URI = "/topic/user/chat";

    // 用户私聊地址前缀
    private static final String PRIVATE_MESSAGE_PREFIX = "/user";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    @MessageMapping("/user/chat")
    public void sendPrivateMessage(Message message) {
        message.setSendDate(new Date());
        message.setMessageType("text");
        logger.info(message.getSendDate() + "," + message.getUserName() + " send a private message to " + message.getReceiver() + ": " + message.getContent());

        // 保存私聊信息（这部分需要根据业务逻辑实现）
        System.out.println("保存好了");
        // 发送私聊消息
        messagingTemplate.convertAndSendToUser(message.getReceiver(), PRIVATE_MESSAGE_PREFIX, message);
    }

}