package com.hjj.homiematching.service;


import com.hjj.homieMatching.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("burgerString","yummy");
        valueOperations.set("numInt",1);
        valueOperations.set("numDouble",2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("burger");
        valueOperations.set("user",user);
        // 查
        Object burger = valueOperations.get("burgerString");
        // 删
        redisTemplate.delete("user");
        Assertions.assertTrue("yummy".equals((String) burger));

    }
}
