package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.FollowMapper;
import com.hjj.homieMatching.model.domain.Follow;
import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.FollowQueryRequest;
import com.hjj.homieMatching.model.vo.FollowVO;
import com.hjj.homieMatching.service.FollowService;
import com.hjj.homieMatching.service.MessageService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 何佳骏
 * @description 针对表【follow(关注表)】的数据库操作Service实现
 * @createDate 2024-07-19 12:46:18
 */
@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
        implements FollowService {

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;

    @Override
    public boolean isFollowed(long followeeId, long followerId) {
        if (followeeId <= 0 || followerId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User followee = userService.getById(followeeId);
        if (followee == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "关注对象不存在");
        }
        return this.lambdaQuery().eq(Follow::getFolloweeId, followeeId).eq(Follow::getFollowerId, followerId).count() > 0;
    }

    @Override
    public boolean addFollow(long followeeId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        if (userId == followeeId) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能关注自己");
        }
        if (this.isFollowed(followeeId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已关注");
        }
        Follow follow = new Follow();
        follow.setFollowerId(userId);
        follow.setFolloweeId(followeeId);
        // todo 增加被关注者粉丝数，增加关注者的关注数
        // 添加关注消息到消息表
        Message message = new Message();
        message.setFromId(userId);
        message.setToId(followeeId);
        message.setType(2);
        message.setText("关注了您");
        messageService.addFollowMessage(message);
        return this.save(follow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFollow(long followeeId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        if (userId == followeeId) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "自己不会关注自己");
        }
        if (!this.isFollowed(followeeId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未关注");
        }
        boolean remove = this.lambdaUpdate().eq(Follow::getFollowerId, userId).eq(Follow::getFolloweeId, followeeId).remove();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", followeeId);
        updateWrapper.setSql("blogNum = blogNum - 1");
        boolean update = userService.update(updateWrapper);
        if (!update || ! remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    @Override
    public List<FollowVO> listFollows(FollowQueryRequest followQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int type = followQueryRequest.getType();
        long userId = followQueryRequest.getUserId();
        int pageSize = followQueryRequest.getPageSize();
        int pageNum = followQueryRequest.getPageNum();
        List<FollowVO> followVOList = null;
        if (type == 0) {
            followVOList = this.lambdaQuery().eq(Follow::getFolloweeId, loginUser.getId())
                    .page(new Page<>(pageNum, pageSize)).getRecords().stream().map(follow -> {
                        User follower = userService.getById(follow.getFollowerId());
                        FollowVO followVO = new FollowVO();
                        BeanUtils.copyProperties(follower, followVO);
                        followVO.setIsFollowed(this.isFollowed(loginUser.getId(), follow.getFollowerId()));
                        return followVO;
                    }).collect(Collectors.toList());
        } else if (type == 1) {
            followVOList = this.lambdaQuery().eq(Follow::getFollowerId, loginUser.getId()).page(new Page<>(pageNum, pageSize)).getRecords().
                    stream().map(follow -> {
                        User followee = userService.getById(follow.getFolloweeId());
                        FollowVO followVO = new FollowVO();
                        BeanUtils.copyProperties(followee, followVO);
                        followVO.setIsFollowed(this.isFollowed(follow.getFolloweeId(), loginUser.getId()));
                        return followVO;
                    }).collect(Collectors.toList());
        } else if (type == 2) {
            followVOList = this.lambdaQuery().eq(Follow::getFolloweeId, userId).
                    page(new Page<>(pageNum, pageSize)).getRecords().stream().map(follow -> {
                        User follower = userService.getById(follow.getFollowerId());
                        FollowVO followVO = new FollowVO();
                        BeanUtils.copyProperties(follower, followVO);
                        followVO.setIsFollowed(this.isFollowed(userId, follow.getFollowerId()));
                        return followVO;
                    }).collect(Collectors.toList());
        } else if (type == 3) {
            followVOList = this.lambdaQuery().eq(Follow::getFollowerId, userId)
                    .page(new Page<>(pageNum, pageSize)).getRecords().stream().map(follow -> {
                        User followee = userService.getById(follow.getFolloweeId());
                        FollowVO followVO = new FollowVO();
                        BeanUtils.copyProperties(followee, followVO);
                        followVO.setIsFollowed(this.isFollowed(follow.getFolloweeId(), userId));
                        return followVO;
                    }).collect(Collectors.toList());
        }
        return followVOList;
    }

}




