package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.FriendMapper;
import com.hjj.homieMatching.model.domain.Friend;
import com.hjj.homieMatching.service.FriendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author 17653
* @description 针对表【friend(好友表)】的数据库操作Service实现
* @createDate 2024-02-15 16:45:45
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

    @Resource
    FriendMapper friendMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFriend(long userId, long friendId) {
        // 查询是否添加了该用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("friendId", friendId);
        Long count1 = friendMapper.selectCount(queryWrapper);
        if (count1 > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已添加该用户");
        }
        // 查询是否添加了该用户
        queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", friendId);
        queryWrapper.eq("friendId", userId);
        Long count2 = friendMapper.selectCount(queryWrapper);
        if (count2 > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已添加该用户");
        }
        // 插入id: userId, friendId: friendId
        Friend friendByUserId = new Friend();
        friendByUserId.setUserId(userId);
        friendByUserId.setFriendId(friendId);
        // 插入id:friendId , friendId: userId（注意添加事务，即要么都添加要么都不添加）
        boolean result1 = this.save(friendByUserId);
        Friend friendByFriendId = new Friend();
        friendByFriendId.setUserId(friendId);
        friendByFriendId.setFriendId(userId);
        // 写入数据库
        boolean result2 = this.save(friendByFriendId);
        return result1 && result2;
    }

}




