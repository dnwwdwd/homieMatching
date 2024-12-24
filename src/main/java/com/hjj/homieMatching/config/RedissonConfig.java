package com.hjj.homieMatching.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedissonConfig {

    private String host = "47.116.208.231";
//    private String host = "localhost";

    private String port = "6379";

    private String password = "13584064183cz";

    @Bean
    public RedissonClient redissonClient(){
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(0).setPassword(password);//设置单个服务器，设置地址，选择数据库
//        config.useSingleServer().setAddress(redisAddress).setDatabase(0);//设置单个服务器，设置地址，选择数据库
        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
