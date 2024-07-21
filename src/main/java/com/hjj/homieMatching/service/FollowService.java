package com.hjj.homieMatching.service;

import com.hjj.homieMatching.model.domain.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 何佳骏
* @description 针对表【follow(关注表)】的数据库操作Service
* @createDate 2024-07-19 12:46:18
*/
public interface FollowService extends IService<Follow> {

    boolean isFollowed(long userId, long followerId);

    boolean addFollow(long followeeId, HttpServletRequest request);

    boolean deleteFollow(long followeeId, HttpServletRequest request);
    
}
