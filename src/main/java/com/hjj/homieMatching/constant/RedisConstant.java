package com.hjj.homieMatching.constant;

public interface RedisConstant {
    String SYSTEM_ID = "homieMatching:team:join";
    String USER_JOIN_TEAM = "homieMatching:team:join:";
    String USER_GEO_KEY = "homiemacthing:user:geo";
    String USER_ADD_KEY = "homiematching:user:add";
    String USER_RECOMMEND_KEY = "homieMatching:user:recommend";

    String REDIS_LIMITER_REGISTER = "homieMatching:limiter:register:";
    String REDIS_BLOG_STAR_KEY = "homieMatching:blog:star:";
    String REDIS_BLOG_LIKE_KEY = "homieMatching:blog:like:";
    String REDIS_BLOG_VIEW_KEY = "homieMatching:blog:view:";
    String REDIS_USER_LIKE_BLOG_KEY = "homieMatching:user:like:blog:";
    String REDIS_USER_VIEW_BLOG_KEY = "homieMatching:user:view:blog:";
    String REDIS_USER_STAR_BLOG_KEY = "homieMatching:user:star:blog:";
    String USER_SIGNIN_KEY = "homieMatching:user:signin:";
    String BLOG_BLOOM_FILTER_KEY = "homieMatching:blog:bloomfilter";
    String USER_BLOOM_FILTER_KEY = "homieMatching:user:bloomfilter";
    String BLOG_COVER_IMAGE_UPLOAD_KEY = "homieMatching:blog:cover:image:upload:";
    String BLOG_IMAGE_UPLOAD_KEY = "homieMatching:blog:image:upload:";
    /**
     * 用户推荐缓存
     */
    /**
     * 最小缓存随机时间
     */
    public static final int MINIMUM_CACHE_RANDOM_TIME = 2;
    /**
     * 最大缓存随机时间
     */
    public static final int MAXIMUM_CACHE_RANDOM_TIME = 3;
    /**
     * 缓存时间偏移
     */
    public static final int CACHE_TIME_OFFSET = 10;
}
