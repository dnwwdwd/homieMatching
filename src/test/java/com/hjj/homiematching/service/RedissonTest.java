package com.hjj.homiematching.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.UserService;
import io.swagger.models.auth.In;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    UserService userService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Test
    public void test(){
        // list，数据存在本地 JVM 内存中
/*        List<String> list = new ArrayList<>();
        list.add("yupi");
        list.get(0);
        list.remove(0);*/

        // 数据存在 redis 的内存中
        RList<Object> rList = redissonClient.getList("test-list");
        System.out.println("rList:" + rList.get(0));
        rList.remove(0);
        // map
        Map<String, Integer> map = new HashMap();
        map.put("yupi", 10);
        map.get("yupi");

        RMap<String, Integer> rMap = redissonClient.getMap("test-map");
        rMap.put("yupi", 10);
        rMap.get("yupi");
        // set
        // stack
    }
    @Test
    public void watchDogTest(){
        String doCacheLockId = String.format("%s:precachejob:docache:lock", RedisConstant.SYSTEM_ID);
        RLock lock = redissonClient.getLock(doCacheLockId);
        try {
            // 只有一个线程能够获取锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);
                System.out.println(Thread.currentThread().getId() + "我拿到锁了");
            }
        } catch (InterruptedException e) {
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
