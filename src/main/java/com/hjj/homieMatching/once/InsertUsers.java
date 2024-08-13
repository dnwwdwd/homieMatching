package com.hjj.homieMatching.once;


import com.hjj.homieMatching.mapper.UserMapper;
import com.hjj.homieMatching.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入数据
     */
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 10000000;
        for (int i=0; i < INSERT_NUM; i++) {
            User user =new User();
            user.setId(0L);
            user.setUsername("假用户");
            user.setUserAccount("fakehjj");
            user.setAvatarUrl("https://tse3-mm.cn.bing.net/th/id/OIP-C.q0uY4jyb-dsKVGksY4LEjAHaHa?rs=1&pid=ImgDetMain");
            user.setGender(0);
            user.setProfile("");
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("14654@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setTags("[]");
            userMapper.insert(user);

        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    public static void main(String[] args) {
        new InsertUsers().doInsertUsers();
    }
}
