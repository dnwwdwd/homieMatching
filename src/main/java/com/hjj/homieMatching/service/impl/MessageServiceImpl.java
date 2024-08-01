package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.MessageQueryRequest;
import com.hjj.homieMatching.model.vo.InteractionMessageVO;
import com.hjj.homieMatching.model.vo.MessageVO;
import com.hjj.homieMatching.service.BlogService;
import com.hjj.homieMatching.service.MessageService;
import com.hjj.homieMatching.mapper.MessageMapper;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 何佳骏
* @description 针对表【message】的数据库操作Service实现
* @createDate 2024-07-31 20:19:53
*/
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private BlogService blogService;

    @Override
    public boolean addStarMessage(Message message) {
        Long fromId = message.getFromId();
        Long toId = message.getToId();
        String text = message.getText();
        Long blogId = message.getBlogId();
        User user = userService.getById(fromId);
        Blog blog = blogService.getById(blogId);
        message.setAvatarUrl(user.getAvatarUrl());
        message.setText(user.getUsername() + text + blog.getTitle());
        boolean save = this.save(message);
        if (!save) {
            log.error("用户：{} 收藏：{} 的博客：{} 后，添加收藏消息到消息表失败了！", fromId, toId, blogId);
        }
        return save;
    }

    @Override
    public boolean addLikeMessage(Message message) {
        Long fromId = message.getFromId();
        Long toId = message.getToId();
        String text = message.getText();
        Long blogId = message.getBlogId();
        User user = userService.getById(fromId);
        Blog blog = blogService.getById(blogId);
        message.setAvatarUrl(user.getAvatarUrl());
        message.setText(user.getUsername() + text + blog.getTitle());
        boolean save = this.save(message);
        if (!save) {
            log.error("用户：{} 点赞:{} 的博客：{} 后，添加点赞消息到消息表失败了！", fromId, toId, blogId);
        }
        return save;
    }

    @Override
    public boolean addFollowMessage(Message message) {
        Long fromId = message.getFromId();
        Long toId = message.getToId();
        String text = message.getText();
        User user = userService.getById(fromId);
        message.setAvatarUrl(user.getAvatarUrl());
        message.setText(user.getUsername() + text);
        boolean save = this.save(message);
        if (!save) {
            log.error("用户：{} 关注用户：{} 时发送关注消息失败了！", fromId, toId);
        }
        return save;
    }

    @Override
    public InteractionMessageVO listInteractionMessage(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long starMessageNum = this.baseMapper.selectCount(new QueryWrapper<Message>().eq("toId", userId).eq("type", 0));
        long likeMessageNum = this.baseMapper.selectCount(new QueryWrapper<Message>().eq("toId", userId).eq("type", 1));
        long followMessageNum = this.baseMapper.selectCount(new QueryWrapper<Message>().eq("toId", userId).eq("type", 2));
        return new InteractionMessageVO(likeMessageNum, starMessageNum, followMessageNum);
    }

    @Override
    public List<MessageVO> listMessages(MessageQueryRequest messageQueryRequest, HttpServletRequest request) {
        Integer type = messageQueryRequest.getType();
        User loginUser = userService.getLoginUser(request);
        if (type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("toId", loginUser.getId());
        queryWrapper.eq("type", type);
        List<Message> messageList = this.list(queryWrapper);
        List<MessageVO> messageVOList = messageList.stream().map(message -> {
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message, messageVO);
            return messageVO;
        }).collect(Collectors.toList());
        // todo 查询的消息变为已读
        return messageVOList;
    }

}




