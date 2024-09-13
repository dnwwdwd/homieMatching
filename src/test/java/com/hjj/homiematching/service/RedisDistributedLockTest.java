package com.hjj.homiematching.service;


import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisDistributedLockTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RLock lock = redissonClient.getLock("test");
        try {
            if (lock.tryLock(0, 300000, TimeUnit.MILLISECONDS)) {
                System.out.println(Thread.currentThread().getId() + "抢到了分布式锁");
                // 执行对应的业务逻辑（比如扣库存，扣余额）
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 一定要将锁的释放放在 finally 块中，这样保证无论是否发生异常，都能释放锁
                System.out.println(Thread.currentThread().getId() + "释放分布式锁成功");
            }
        }
    }
}
