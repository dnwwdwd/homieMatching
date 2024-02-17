package com.hjj.homiematching.service;

import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ImportUserGEO {
    @Resource
    RedisTemplate redisTemplate;

    @Resource
    UserService userService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private List<User> keyUsersList;

    @PostConstruct
    public void initialUserList() {
         this.keyUsersList = userService.list();
         if (this.keyUsersList.size() <= 0) {
             System.out.println("keyUsersList is empty");
         }
    }


    @Test
    public void importUserGEOByRedis() {
        List<User> userList = userService.list();
        String key = RedisConstant.USER_GEO_KEY;
        List<RedisGeoCommands.GeoLocation<String>> locationList = new ArrayList<>(userList.size());
        for (User user : userList) {
            locationList.add(new RedisGeoCommands.GeoLocation<>(String.valueOf(user.getId()), new Point(user.getLongitude(),
                    user.getDimension())));
        }
        stringRedisTemplate.opsForGeo().add(key, locationList);
    }

    @Test
    public void getUserGeo() {
//        String key = RedisConstant.USER_GEO_KEY;
//        List<User> userList = userService.list();
//        List<Double> longitudeList = userList.stream().map(User::getLongitude).collect(Collectors.toList());
//        System.out.println(longitudeList);
//        List<Double> dimensionList = userList.stream().map(User::getDimension).collect(Collectors.toList());
//        User loginUser = userService.getLoginUser(request);
//        Double longitude = loginUser.getLongitude();
//        Double dimension = loginUser.getDimension();
//
//        stringRedisTemplate.opsForGeo().search();
//
//
        String key = RedisConstant.USER_GEO_KEY;
        List<User> userList = userService.list();


// 计算每个用户与登录用户的距离
//        for (User user : userList) {
//            Distance distance = stringRedisTemplate.opsForGeo().distance(key,
//                    "1", String.valueOf(user.getId()), RedisGeoCommands.DistanceUnit.KILOMETERS);
//            System.out.println("User: " + user.getId() + ", Distance: " +
//                    distance.getValue() + " " + distance.getUnit());
//        }

        System.out.println(keyUsersList);
    }
}
