package com.hjj.homiematching.service;

import com.hjj.homieMatching.mapper.UserMapper;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UserServiceTest {
    @Resource
    UserMapper userMapper;
    @Resource
    UserService userService;
    @Test
    public void testSearchUsersByTags(){
    /*        List<String> tagNameList= Arrays.asList("java","python");
            List<User> userList = userService.searchUsersByTags(tagNameList);
            System.out.println(userList);*/
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        System.out.println(userList.size());
        Assert.assertNotNull(userList);
    }
}
