package com.hjj.homiematching.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.constant.UserConstant;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class RecommendUsersTest {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void recommendUsers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", 1);
        IPage<User> page = new Page<>(1, 8);
        IPage<User> userIPage = userService.page(page, queryWrapper);

        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;

        // 将User转换为UserVO
        List<UserVO> userVOList = userIPage.getRecords().stream()
                .map(user -> {
                    // 查询距离
                    Distance distance = stringRedisTemplate.opsForGeo().distance(redisUserGeoKey,
                            String.valueOf(1), String.valueOf(user.getId()),
                            RedisGeoCommands.DistanceUnit.KILOMETERS);
                    double value = distance.getValue();

                    // 创建UserVO对象并设置属性
                    UserVO userVO = new UserVO();
                    userVO.setId(user.getId());
                    userVO.setUsername(user.getUsername());
                    userVO.setUserAccount(user.getUserAccount());
                    userVO.setAvatarUrl(user.getAvatarUrl());
                    userVO.setGender(user.getGender());
                    userVO.setProfile(user.getProfile());
                    userVO.setPhone(user.getPhone());
                    userVO.setEmail(user.getEmail());
                    userVO.setUserStatus(user.getUserStatus());
                    userVO.setCreateTime(user.getCreateTime());
                    userVO.setUpdateTime(user.getUpdateTime());
                    userVO.setUserRole(user.getUserRole());
                    userVO.setTags(user.getTags());
                    userVO.setDistance(value); // 设置距离值
                    return userVO;
                })
                .collect(Collectors.toList());
        List<String> userVOJsonList = userVOList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        Long aLong = stringRedisTemplate.opsForList().rightPushAll("homieMatching:user:recommend:test", userVOJsonList);
        System.out.println(aLong);
    }

    @Test
    public void getUserByList() {
        int start = 3;
        int end = 3 + 3;
        List<String> range = stringRedisTemplate.opsForList().range(RedisConstant.USER_RECOMMEND_KEY + ":1", start, end);
        System.out.println(range);
        System.out.println(range.size());
    }
}
