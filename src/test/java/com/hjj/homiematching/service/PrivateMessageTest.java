package com.hjj.homiematching.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjj.homieMatching.model.domain.Friend;
import com.hjj.homieMatching.service.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class PrivateMessageTest {

    @Resource
    private FriendService friendService;

    @Test
    public void test() {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",1);
        List<Long> friendList = friendService.list(queryWrapper).stream().map(Friend::getFriendId).collect(Collectors.toList());
        System.out.println(friendList);
    }
}
