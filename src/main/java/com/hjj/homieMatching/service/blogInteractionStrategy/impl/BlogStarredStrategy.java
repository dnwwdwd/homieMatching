package com.hjj.homieMatching.service.blogInteractionStrategy.impl;

import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.service.blogInteractionStrategy.BlogInteractionStrategy;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

public class BlogStarredStrategy implements BlogInteractionStrategy {

    /**
     * 查询自己收藏的博客
     * @return
     */
    @Override
    public Set<String> interactionMethod(BlogQueryRequest blogQueryRequest, StringRedisTemplate stringRedisTemplate, long userId) {
        return stringRedisTemplate.opsForSet().members(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId);
    }
}
