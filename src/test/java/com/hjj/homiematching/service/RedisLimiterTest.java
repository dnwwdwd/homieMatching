package com.hjj.homiematching.service;

import com.hjj.homieMatching.manager.RedisLimiterManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisLimiterTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    public void test() {
        for (int i = 0; i < 100; i++) {
            redisLimiterManager.doRateLimiter("test", 1, 1);
        }
        System.out.println("调用成功");
    }
}
