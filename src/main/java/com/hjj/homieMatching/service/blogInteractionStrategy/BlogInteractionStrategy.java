package com.hjj.homieMatching.service.blogInteractionStrategy;

import com.hjj.homieMatching.model.request.BlogQueryRequest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

public interface BlogInteractionStrategy {

    Set<String> interactionMethod(BlogQueryRequest blogQueryRequest, StringRedisTemplate stringRedisTemplate, long userId);

}
