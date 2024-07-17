package com.hjj.homieMatching.manager;

import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimiter(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.setRate(RateType.OVERALL, 1, 1, RateIntervalUnit.MINUTES);
        boolean b = rateLimiter.tryAcquire();
        if (!b) {
            log.error(key + "请求次数过多，获取注册锁失败");
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
