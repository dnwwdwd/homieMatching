package com.hjj.homieMatching.manager;

import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RedisBloomFilter {

    @Resource
    private RedissonClient redissonClient;

    public void createBloomFilter(String key, int expectedInsertions, double fpp) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
        boolean b = bloomFilter.tryInit(expectedInsertions, fpp);
        if (!b) {
            log.error("布隆过滤器：{} 初始化失败", key);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "布隆过滤器初始化失败");
        }

    }

    public boolean isContained(String key, long id) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
        return bloomFilter.contains(id);
    }

    public void addValueToFilter(String key, long id) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
        boolean add = bloomFilter.add(id);
        if (!add) {
            log.error("布隆过滤器：{} 添加元素（博客/用户）：{} 失败", key, id);
        }
    }
}
