package com.hjj.homiematching;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.mapper.BlogMapper;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.vo.BlogVO;
import com.hjj.homieMatching.service.BlogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@SpringBootTest
class HomieMatchingApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private BlogService blogService;

    @Test
    void contextLoads() {
        Set<String> members = stringRedisTemplate.opsForSet().members(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + 2);
        System.out.println(members);

    }

    @Test
    public void test() {
        System.out.println(blogService.page(new Page<>(1, 4)).getRecords().stream().map(Blog::getId).collect(Collectors.toList()));
        System.out.println(blogService.page(new Page<>(2, 4)).getRecords().stream().map(Blog::getId).collect(Collectors.toList()));
        System.out.println(blogService.page(new Page<>(3, 4)).getRecords().stream().map(Blog::getId).collect(Collectors.toList()));
    }

    @Test
    public void selectBlogByPageTest() {
        BlogQueryRequest blogQueryRequest = new BlogQueryRequest();
        blogQueryRequest.setPageNum(1);
        blogQueryRequest.setPageSize(4);
        List<BlogVO> blogVOList = blogService.listBlogs(blogQueryRequest, null);
        System.out.println(blogVOList);
    }
}
