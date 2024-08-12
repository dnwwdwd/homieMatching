package com.hjj.homiematching.service;

import com.hjj.homieMatching.manager.RedisBloomFilter;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.BlogService;
import com.hjj.homieMatching.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class BloomFilterTest {

    @Resource
    private RedisBloomFilter redisBloomFilter;

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Test
    public void addAllUsersToFilter() {
        List<Long> ids = userService.list().stream().map(User::getId).collect(Collectors.toList());
        for (long id : ids) {
            redisBloomFilter.addUserToFilter(id);
        }

    }

    @Test
    public void addAllBlogsToFilter() {
        List<Long> ids = blogService.list().stream().map(Blog::getId).collect(Collectors.toList());
        for (long id : ids) {
            redisBloomFilter.addBlogToFilter(id);
        }
    }

}
