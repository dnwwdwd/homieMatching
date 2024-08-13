package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.FriendMapper;
import com.hjj.homieMatching.mapper.UserMapper;
import com.hjj.homieMatching.model.domain.Friend;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.FriendQueryRequest;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.FriendService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

    @Resource
    UserMapper userMapper;

    @Resource
    UserService userService;

    @Resource
    FriendMapper friendMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "自己不能添加自己为好友'");
        }
        // 设置锁名称，锁范围是同一个人加同一个人为好友
        String addUserLock = RedisConstant.USER_ADD_KEY + userId + friendId;
        RLock lock = redissonClient.getLock(addUserLock);
        boolean result1 = false;
        boolean result2 = false;
        try{
            // 尝试获取锁
           if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
               log.info(Thread.currentThread().getId() + "我拿到锁了");
               // 查询是否添加了该用户
               QueryWrapper<Friend> queryWrapper = new QueryWrapper();
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
               result1 = this.save(friendByUserId);
               Friend friendByFriendId = new Friend();
               friendByFriendId.setUserId(friendId);
               friendByFriendId.setFriendId(userId);
               // 写入数据库
               result2 = this.save(friendByFriendId);
           }
        } catch (InterruptedException e) {
            log.error("addUser error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info(Thread.currentThread().getId() + "锁已经被释放");
                lock.unlock();
            }
        }
        return result1 && result2;
    }

    @Override
    public List<UserVO> listFriends(Long userId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Friend> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", userId);
        List<Friend> friendList = friendMapper.selectList(queryWrapper);
        List<User> userList = friendList.stream().map(friend -> {
            User user = userMapper.selectById(friend.getFriendId());
            return user;
        }).collect(Collectors.toList());
        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;
        List<UserVO> userVOList = userList.stream().map(user -> {
            Distance distance = stringRedisTemplate.opsForGeo().distance(redisUserGeoKey,
                    String.valueOf(loginUser.getId()), String.valueOf(user.getId()),
                    RedisGeoCommands.DistanceUnit.KILOMETERS);
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setUserAccount(user.getUserAccount());
            userVO.setAvatarUrl(user.getAvatarUrl());
            userVO.setGender(user.getGender());
            userVO.setProfile(user.getProfile());
            userVO.setPhone(user.getPhone());
            userVO.setEmail(user.getEmail());
            userVO.setUserStatus(user.getUserStatus());
            userVO.setCreateTime(user.getCreateTime());
            userVO.setUpdateTime(user.getUpdateTime());
            userVO.setUserRole(user.getUserRole());
            userVO.setTags(user.getTags());
            userVO.setDistance(distance.getValue()); // 设置距离值
            return userVO;
        }).collect(Collectors.toList());
        return userVOList;
    }

    @Override
    public List<UserVO> searchFriends(FriendQueryRequest friendQueryRequest, long userId) {
        String searchParam = friendQueryRequest.getSearchParam();
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        // 查询登录用户的所有好友
        List<Friend> friendList = this.list(queryWrapper);
        List<Long> frinedIdList = friendList.stream().map(Friend::getFriendId).collect(Collectors.toList());
        // 根据 id 和 username 查询目标用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", frinedIdList);
        userQueryWrapper.like("username", searchParam);
        List<User> userList = userService.list(userQueryWrapper);
        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;
        // 查询登录用户和好友的距离
        List<UserVO> userVOList = userList.stream().map(user -> {
            Distance distance = stringRedisTemplate.opsForGeo().distance(redisUserGeoKey,
                    String.valueOf(userId), String.valueOf(user.getId()),
                    RedisGeoCommands.DistanceUnit.KILOMETERS);
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setUserAccount(user.getUserAccount());
            userVO.setAvatarUrl(user.getAvatarUrl());
            userVO.setGender(user.getGender());
            userVO.setProfile(user.getProfile());
            userVO.setPhone(user.getPhone());
            userVO.setEmail(user.getEmail());
            userVO.setUserStatus(user.getUserStatus());
            userVO.setCreateTime(user.getCreateTime());
            userVO.setUpdateTime(user.getUpdateTime());
            userVO.setUserRole(user.getUserRole());
            userVO.setTags(user.getTags());
            userVO.setDistance(distance.getValue()); // 设置距离值
            return userVO;
        }).collect(Collectors.toList());
        return userVOList;
    }
}
