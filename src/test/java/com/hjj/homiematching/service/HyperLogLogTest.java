package com.hjj.homiematching.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class HyperLogLogTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testHyperLogLog() {

    }
}
