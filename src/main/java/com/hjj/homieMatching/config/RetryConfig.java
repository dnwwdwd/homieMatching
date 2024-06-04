package com.hjj.homieMatching.config;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetryConfig {

    @Bean
    public Retryer<Boolean> retryer() {
        return RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 设置出现 Exception 异常就重试
                .retryIfResult(Predicates.equalTo(false)) // 设置结果为 false 重试
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS)) // 设置每次重试间隔为 2s
                .withStopStrategy(StopStrategies.stopAfterAttempt(2)) // 设置重试次数为 2 次，超过 2 次就停止
                .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(3, TimeUnit.SECONDS)) // 设置每次重试的时间限制为 3s
                .build();
    }
}
