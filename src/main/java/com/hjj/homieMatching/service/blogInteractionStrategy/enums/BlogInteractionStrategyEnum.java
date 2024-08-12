package com.hjj.homieMatching.service.blogInteractionStrategy.enums;

import com.hjj.homieMatching.service.blogInteractionStrategy.BlogInteractionStrategy;
import com.hjj.homieMatching.service.blogInteractionStrategy.impl.BlogLikedStrategy;
import com.hjj.homieMatching.service.blogInteractionStrategy.impl.BlogStarredStrategy;
import com.hjj.homieMatching.service.blogInteractionStrategy.impl.BlogViewedStrategy;
import org.apache.commons.lang3.ObjectUtils;

public enum BlogInteractionStrategyEnum {

    MY_STARRED_BLOG(0,"我收藏的博客",new BlogStarredStrategy()),

    MY_LIKED_BLOG(1,"我点赞的博客",new BlogLikedStrategy()),

    MY_VIEWED_BLOG(2,"我浏览的博客",new BlogViewedStrategy()),

    OTHER_STARRED_BLOG(3,"其他人收藏的博客",new BlogStarredStrategy()),

    OTHER_LIKED_BLOG(4,"其他人点赞的博客",new BlogLikedStrategy()),

    OTHER_VIEWED_BLOG(5,"其他人浏览的博客",new BlogViewedStrategy());

    BlogInteractionStrategyEnum(int code, String description, BlogInteractionStrategy blogInteractionStrategy) {
        this.code = code;
        this.description = description;
        this.blogInteractionStrategy = blogInteractionStrategy;
    }

    private final int code;

    private final String description;

    private final BlogInteractionStrategy blogInteractionStrategy;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BlogInteractionStrategy getBlogInteractionStrategy() {
        return blogInteractionStrategy;
    }

    public static BlogInteractionStrategy getStrategyByCode(int code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }
        for (BlogInteractionStrategyEnum anEnum : BlogInteractionStrategyEnum.values()) {
            if (anEnum.code == code) {
                return anEnum.getBlogInteractionStrategy();
            }
        }
        return null;
    }
}
