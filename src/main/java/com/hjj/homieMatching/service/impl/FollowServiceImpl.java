package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.FollowMapper;
import com.hjj.homieMatching.model.domain.Follow;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.FollowService;
import com.hjj.homieMatching.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author 何佳骏
* @description 针对表【follow(关注表)】的数据库操作Service实现
* @createDate 2024-07-19 12:46:18
*/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{

    @Resource
    private UserService userService;

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
        return this.save(follow);
    }

    @Override
    public boolean deleteFollow(long followeeId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        if (userId == followeeId) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "自己不会关注自己");
        }
        if (!this.isFollowed(followeeId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未关注");
        }
        return this.lambdaUpdate().eq(Follow::getFollowerId, userId).eq(Follow::getFolloweeId, followeeId).remove();
    }

}




