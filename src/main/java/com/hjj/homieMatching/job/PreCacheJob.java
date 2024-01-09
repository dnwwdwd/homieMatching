package com.hjj.homieMatching.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    RedissonClient redissonClient;
    // 重点用户
    private List<Long> keyUsersIdList = Arrays.asList(1L);
    // 每天执行，预热缓存推荐用户
    @Scheduled(cron = "0 32 11 * * *")
    synchronized public void doCacheRecommendUser() {
        String doCacheLockId = String.format("%s:precachejob:docache:lock", RedisConstant.SYSTEM_ID);
        RLock lock = redissonClient.getLock(doCacheLockId);
        try {
            // 只有一个线程能够获取锁
            if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                System.out.println(Thread.currentThread().getId() + "我拿到锁了");
                for (Long userId : keyUsersIdList) {
                    // 查数据库数据
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    IPage<User> page = new Page<>(1, 20);
                    IPage<User> userIPage = userService.page(page, queryWrapper);
                    String redisKey = String.format("homieMatching:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try{
                        valueOperations.set(redisKey, userIPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser", e);
            throw new RuntimeException(e);
        } finally { // 不管所是否会失效都会执行下段保证释放锁
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) { // 判断当前的锁是不是当前这个线程加的锁，每次抢锁时都会有一个线程Id，
                // 这个Id会存在redis中，验证线程的id就好了
                System.out.println(Thread.currentThread().getId() + "锁已经释放了");
                lock.unlock(); // 执行业务逻辑后，要释放锁
            }
        }
    }
}
