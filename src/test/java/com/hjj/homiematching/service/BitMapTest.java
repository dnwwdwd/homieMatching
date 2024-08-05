package com.hjj.homiematching.service;

import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.manager.SignInManager;
import com.hjj.homieMatching.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BitMapTest {

    @Resource
    private SignInManager signInManager;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void signInTest() {
        String key = RedisConstant.USER_SIGNIN_KEY + DateUtils.getNowYear() + ":2";
        RBitSet bitSet = redissonClient.getBitSet(key);
        bitSet.set(215, true);
        System.out.println(signInManager.isSignIn(key));

    }

    @Test
    public void listSignInInfo() {
        String key = RedisConstant.USER_SIGNIN_KEY + DateUtils.getNowYear() + ":2";
        RBitSet bitSet = redissonClient.getBitSet(key);
        List<Integer> signedInDates = new ArrayList<>();
        List<Integer> unSignedInDates = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                signedInDates.add(i);
            } else {
                unSignedInDates.add(i);
            }
        }
        System.out.println(signedInDates);
        System.out.println(unSignedInDates);


        LocalDate today = LocalDate.now();
        System.out.println("今日日期: " + today);

        // 距离今日的天数
        int daysFromToday = 5;

        // 计算具体的日期
        LocalDate targetDate = today.minusDays(daysFromToday);
        java.util.Date date = Date.from(targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println("目标日期: " + targetDate);
    }

}
