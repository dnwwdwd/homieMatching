package com.hjj.homieMatching.manager;

import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimiter(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.setRate(RateType.OVERALL, 1, 1, RateIntervalUnit.MINUTES);
        boolean b = rateLimiter.tryAcquire();
        if (!b) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
