package com.hjj.homieMatching.constant;

public interface RedisConstant {
    String SYSTEM_ID = "homieMatching:team:join";
    String USER_JOIN_TEAM = "homieMatching:team:join:";
    String USER_GEO_KEY = "homiemacthing:user:geo";
    String USER_ADD_KEY = "homiematching:user:add";
    String USER_RECOMMEND_KEY = "homieMatching:user:recommend";
    public static final String LOGIN_USER_KEY = "homieMatching:login:token:";

    public static final Long LOGIN_USER_TTL = 15L;
    /**
     * 注册验证码键
     */
    public static final String REGISTER_CODE_KEY = "homieMatching:register:";
    /**
     * 注册验证码过期时间
     */
    public static final Long REGISTER_CODE_TTL = 15L;
    /**
     * 用户更新电话键
     */
    public static final String USER_UPDATE_PHONE_KEY = "homieMatching:user:update:phone:";
    /**
     * 用户更新电话过期时间
     */
    public static final Long USER_UPDATE_PHONE_TTL = 15L;
    /**
     * 用户更新邮件键
     */
    public static final String USER_UPDATE_EMAIL_KEY = "homieMatching:user:update:email:";
    /**
     * 用户更新邮件过期时间
     */
    public static final Long USER_UPDATE_EMAIL_TTL = 15L;
    /**
     * 用户忘记密码键
     */
    public static final String USER_FORGET_PASSWORD_KEY = "homieMatching:user:forget:";
    /**
     * 用户忘记密码过期时间
     */
    public static final Long USER_FORGET_PASSWORD_TTL = 15L;
    /**
     * 博客推送键
     */
    public static final String BLOG_FEED_KEY = "homieMatching:feed:blog:";
    /**
     * 新博文消息键
     */
    public static final String MESSAGE_BLOG_NUM_KEY = "homieMatching:message:blog:num:";
    /**
     * 新点赞消息键
     */
    public static final String MESSAGE_LIKE_NUM_KEY = "homieMatching:message:like:num:";
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
