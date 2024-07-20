package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.BlogMapper;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.vo.BlogUserVO;
import com.hjj.homieMatching.model.vo.BlogVO;
import com.hjj.homieMatching.model.vo.LikeRequest;
import com.hjj.homieMatching.model.vo.StarRequest;
import com.hjj.homieMatching.service.BlogService;
import com.hjj.homieMatching.service.FollowService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hejiajun
 * @description 针对表【blog(博客表)】的数据库操作Service实现
 * @createDate 2024-07-17 16:25:17
 */
@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {

    private static final double TIME_UNIT = 1000.0;

    @Resource
    private UserService userService;

    @Resource
    private FollowService followService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long addBlog(BlogAddRequest blogAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        String title = blogAddRequest.getTitle();
        String coverImage = blogAddRequest.getCoverImage();
        List<String> images = blogAddRequest.getImages();
        String content = blogAddRequest.getContent();
        List<String> tags = blogAddRequest.getTags();
        // 1.校验
        if (StringUtils.isEmpty(title) || title.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(coverImage) || coverImage.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (images.size() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片过多");
        }
        if (StringUtils.isEmpty(content) || content.length() > 100000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容长度超过 100000");
        }
        if (tags.size() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不得超过 5 个");
        }
        // 2.保存至数据库
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setCoverImage(coverImage);
        blog.setContent(content);
        blog.setUserId(userId);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        for (int i = 0; i < tags.size(); i++) {
            if (i < tags.size() - 1) {
                stringBuffer.append(tags.get(i) + ",");
            } else {
                stringBuffer.append(tags.get(i));
            }
        }
        stringBuffer.append(']');
        String tagList = stringBuffer.toString();
        stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        for (int i = 0; i < images.size(); i++) {
            if (i < images.size() - 1) {
                stringBuffer.append(images.get(i) + ",");
            } else {
                stringBuffer.append(images.get(i));
            }
        }
        stringBuffer.append(']');
        String imageList = stringBuffer.toString();
        blog.setTags(tagList);
        blog.setImages(imageList);
        boolean save = this.save(blog);
        if (!save) {
            log.error("用户：{} 创建博客失败", userId);
        }
        return blog.getId();
    }

    @Override
    public List<BlogVO> listBlogs(BlogQueryRequest blogQueryRequest) {
        String title = blogQueryRequest.getTitle();
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(title), "title", title);
        int pageSize = blogQueryRequest.getPageSize();
        int pageNum = blogQueryRequest.getPageNum();
        Page<Blog> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Blog> blogList = page.getRecords();
        List<BlogVO> blogVOList = blogList.stream().map(blog -> {
            BlogVO blogVO = new BlogVO();
            BeanUtils.copyProperties(blog, blogVO);
            User user = userService.getById(blog.getUserId());
            BlogUserVO blogUserVO = new BlogUserVO();
            blogUserVO.setUsername(user.getUsername());
            blogVO.setBlogUserVO(blogUserVO);
            return blogVO;
        }).collect(Collectors.toList());
        return blogVOList;
    }

    @Override
    public BlogVO getBlogDetailById(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long blogId = blog.getId();
        BlogVO blogVO = new BlogVO();
        BlogUserVO blogUserVO = new BlogUserVO();
        BeanUtils.copyProperties(blog, blogVO);
        Long blogUserId = blog.getUserId();
        User user = userService.getById(blogUserId);
        BeanUtils.copyProperties(user, blogUserVO);
        // 查询文章作者的文章数、粉丝数、总浏览量和是否关注他
        // todo 将查询作者的文章数，粉丝数，总浏览量都改为查询数据库（发布文章后，关注后，浏览后都要把数据存在数据库中）
        blogUserVO.setBlogNum(userService.hasBlogCount(blogUserId));
        blogUserVO.setFanNum(userService.hasFollowerCount(blogUserId));
        blogUserVO.setIsFollowed(followService.isFollowed(blogUserId, userId));
        // todo 查询作者的总浏览量

        blogVO.setBlogUserVO(blogUserVO);
        // 查询文章是否点赞、收藏
        blogVO.setIsLiked(isLiked(blogId, userId));
        blogVO.setIsStarred(isStarred(blogId, userId));
        // todo 查询文章的相关评论
        Boolean isViewed = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_VIEW_KEY + blogId, String.valueOf(userId));
        // 增加文章浏览量（前提没浏览过），增加用户的浏览记录（此处就不用增加作者的总浏览量了，因为用户的总浏览量是所有文章浏览量之和）
        if (isViewed != null && !isViewed) {
            stringRedisTemplate.opsForSet().add(RedisConstant.REDIS_BLOG_VIEW_KEY + blogId, String.valueOf(userId));
            blog = new Blog();
            blog.setId(blog.getId());
            blog.setViewNum(blog.getViewNum() + 1);
            // 更新文章的浏览量，下次直接查询直接从数据库查
            boolean updateBlog = this.updateById(blog);
            if (!updateBlog) {
                log.error("用户：{} 浏览博客：{} 后，更新博客的浏览量失败了", userId, blogId);
            }
        }
        // 不管有没有浏览过，都要增加用户的浏览记录，由于 Redis 的 ZSet 天生不存在重复元素的特性，所以就无需判断是否存在了
        stringRedisTemplate.opsForZSet().add(RedisConstant.REDIS_USER_VIEW_BLOG_KEY + userId,
                        String.valueOf(blogId), Instant.now().toEpochMilli() / TIME_UNIT);
        return blogVO;
    }

    @Override
    public boolean deleteBlog(DeleteRequest deleteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long id = deleteRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id);
        long count = this.count(queryWrapper);
        if (count < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }
        boolean b = this.removeById(id);
        if (!b) {
            log.error("用户：{} 删除文章 {} 失败", userId, id);
        }
        return b;
    }

    @Override
    public boolean starBlog(StarRequest starRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long blogId = starRequest.getBlogId();
        boolean starred = starRequest.getIsStarred();
        Long blogCount = this.lambdaQuery().eq(Blog::getId, blogId).count();
        // 校验参数
        if (blogCount < 1) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "文章不存在");
        }
        if (starred) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已收藏");
        }
        // 判断用户是否收藏过该博客
        Boolean b1 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        Boolean b2 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        }
        // 记录用户收藏了哪些帖子
        Long count1 = stringRedisTemplate.opsForSet()
                .add(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        // 记录博客被哪些用户收藏了
        Long count2 = stringRedisTemplate.opsForSet()
                .add(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        if (count1 == null || count1 < 1) {
            log.error("用户：{} 收藏博客：{} 失败了！", userId, blogId);
        }
        if (count2 == null || count2 < 1) {
            log.error("博客：{} 收藏添加用户：{} 失败了！", blogId, userId);
        }
        return (count1 != null && count1 >= 1) && (count2 != null && count2 >= 1);
    }

    @Override
    public boolean likeBlog(LikeRequest likeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long blogId = likeRequest.getBlogId();
        boolean liked = likeRequest.getIsLiked();
        Long blogCount = this.lambdaQuery().eq(Blog::getId, blogId).count();
        // 校验参数
        if (blogCount < 1) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "文章不存在");
        }
        if (liked) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已点赞过");
        }
        // 判断用户是否点赞过该博客
        Boolean b1 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        Boolean b2 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        }
        // 记录用户点赞了哪些帖子
        Long count1 = stringRedisTemplate.opsForSet()
                .add(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        // 记录博客被哪些用户点赞了
        Long count2 = stringRedisTemplate.opsForSet()
                .add(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        if (count1 == null || count1 < 1) {
            log.error("用户：{} 点赞博客：{} 失败了！", userId, blogId);
        }
        if (count2 == null || count2 < 1) {
            log.error("博客：{} 点赞添加用户：{} 失败了！", blogId, userId);
        }
        return (count1 != null && count1 >= 1) && (count2 != null && count2 >= 1);
    }

    @Override
    public int blogHasLikes(long blogId) {
        return stringRedisTemplate.opsForSet().size(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId).intValue();
    }

    @Override
    public int blogHasStars(long blogId) {
        return stringRedisTemplate.opsForSet().size(RedisConstant.REDIS_BLOG_STAR_KEY + blogId).intValue();
    }


    @Override
    public boolean isStarred(long blogId, long userId) {
        Boolean b1 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        Boolean b2 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        // 文章收藏 set 和用户收藏 set 比如都存在对方，否则删除
        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        }
        return b1 != null && b1 && b2 != null &&b2;
    }

    @Override
    public boolean isLiked(long blogId, long userId) {
        Boolean b1 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        Boolean b2 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        // 文章收藏 set 和用户收藏 set 比如都存在对方，否则删除
        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        }
        return b1 != null && b1 && b2 != null &&b2;
    }



}




