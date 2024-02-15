package com.hjj.homiematching.service;

import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ImportUserGEO {

    @Resource
    UserService userService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void importUserGEOByRedis() {
        List<User> userList = userService.list();
        String key = "homiemacthing:user:geo";
        List<RedisGeoCommands.GeoLocation<String>> locationList = new ArrayList<>(userList.size());
        for (User user : userList) {
            locationList.add(new RedisGeoCommands.GeoLocation<>(String.valueOf(user.getId()), new Point(user.getLongitude(), user.getDimension())));
        }
        stringRedisTemplate.opsForGeo().add(key, locationList);
        System.out.println("你好");
    }
}
