package com.hjj.homieMatching.service.blogInteractionStrategy.impl;

import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.service.blogInteractionStrategy.BlogInteractionStrategy;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

public class BlogViewedStrategy implements BlogInteractionStrategy {

    /**
     * 查询自己浏览的博客
     * @return
     */
    @Override
    public Set<String> interactionMethod(BlogQueryRequest blogQueryRequest, StringRedisTemplate stringRedisTemplate, long userId) {
        return stringRedisTemplate.opsForZSet().range(RedisConstant.REDIS_USER_VIEW_BLOG_KEY + userId, 0, -1);
    }
}
