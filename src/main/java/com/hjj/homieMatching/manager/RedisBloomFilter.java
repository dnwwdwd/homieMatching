package com.hjj.homieMatching.manager;

import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RedisBloomFilter {

    @Resource
    private RBloomFilter<Long> blogBloomFilter;

    @Resource
    private RBloomFilter<Long> userBloomFilter;

    public boolean userIsContained(long id) {
        return userBloomFilter.contains(id);
    }


    public boolean blogIsContained(long id) {
        return blogBloomFilter.contains(id);
    }

    public void addUserToFilter(long id) {
        boolean add = userBloomFilter.add(id);
        if (!add) {
            log.error("用户布隆过滤器：{} 添加元素（博客/用户）：{} 失败", userBloomFilter.getName(), id);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    public void addBlogToFilter(long id) {
        boolean add = blogBloomFilter.add(id);
        if (!add) {
            log.error("文章布隆过滤器：{} 添加元素（博客/用户）：{} 失败", blogBloomFilter.getName(), id);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

}
