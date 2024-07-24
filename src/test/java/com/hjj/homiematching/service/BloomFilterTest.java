package com.hjj.homiematching.service;

import cn.hutool.bloomfilter.BloomFilterUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class BloomFilterTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("user:follow:1");
        long expectedInsertions = bloomFilter.getExpectedInsertions();
        System.out.println(expectedInsertions);
        bloomFilter.getFalseProbability();
        bloomFilter.getHashIterations();
        bloomFilter.tryInit();
        bloomFilter.getSize();
        bloomFilter.addListener();
        bloomFilter.copy();
        bloomFilter.clearExpire();
    }
}
