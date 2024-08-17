package com.hjj.homieMatching.job;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    RedissonClient redissonClient;
    // 重点用户
    private List<Long> userIdList;

    @PostConstruct
    public void initialKeyUserIdList() {
        List<User> userList = userService.list();
        if (CollectionUtils.isEmpty(userList)) {
            log.error("缓存预热时：用户列表为空");
        }
        this.userIdList = userList.stream().map(User::getId).collect(Collectors.toList());
    }

    // 每天执行，预热缓存推荐用户
    @Scheduled(cron = "0 00 00 * * *")
    synchronized public void doCacheRecommendUser() {
        String doCacheLockId = String.format("%s:precachejob:docache:lock", RedisConstant.SYSTEM_ID);
        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;

        RLock lock = redissonClient.getLock(doCacheLockId);
        try {
            // 只有一个线程能够获取锁
            if (lock.tryLock(0, 300000, TimeUnit.MILLISECONDS)) {
                log.info(Thread.currentThread().getId() + "我拿到锁了");
                for (Long userId : userIdList) {
                    // 查数据库数据
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    IPage<User> page = new Page<>(1, 20);
                    IPage<User> userIPage = userService.page(page, queryWrapper);
                    String redisKey = RedisConstant.USER_RECOMMEND_KEY + ":" + userId;
                    List<UserVO> userVOList = userIPage.getRecords().stream()
                            .map(user -> {
                                Distance distance = stringRedisTemplate.opsForGeo()
                                        .distance(redisUserGeoKey, String.valueOf(userId),
                                                String.valueOf(user.getId()), RedisGeoCommands.DistanceUnit.KILOMETERS);
                                UserVO userVO = new UserVO();
                                user.setId(user.getId());
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
                                userVO.setDistance(distance.getValue());
                                return userVO;
                            }).collect(Collectors.toList());
                    List<String> list = userVOList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
                    // 写缓存
                    try{
                        stringRedisTemplate.opsForList().rightPushAll(redisKey, list);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
            throw new RuntimeException(e);
        } finally { // 不管所是否会失效都会执行下段保证释放锁
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) { // 判断当前的锁是不是当前这个线程加的锁，每次抢锁时都会有一个线程Id，
                // 这个Id会存在redis中，验证线程的id就好了
                log.info(Thread.currentThread().getId() + "锁已经释放了");
                lock.unlock(); // 执行业务逻辑后，要释放锁
            }
        }
    }
}
