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
        loginUser = userService.getById(userId);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (title.length() < 1 || title.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(coverImage) || coverImage.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (images.size() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片过多");
        }
        if (StringUtils.isBlank(content) || content.length() > 100000) {
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
            stringBuffer.append('"').append(tags.get(i)).append('"');
            if (i < tags.size() - 1) {
                stringBuffer.append(',');
            }
        }
        stringBuffer.append(']');
        String tagList = stringBuffer.toString();
        stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        for (int i = 0; i < images.size(); i++) {
            stringBuffer.append('"').append(images.get(i)).append('"');
            if (i < images.size() - 1) {
                stringBuffer.append(',');
            }
        }
        stringBuffer.append(']');
        String imageList = stringBuffer.toString();
        blog.setTags(tagList);
        blog.setImages(imageList);
        boolean save = this.save(blog);
        if (!save) {
            log.error("用户：{} 创建博客失败", userId);
        } else {
            User user = new User();
            user.setId(userId);
            user.setBlogNum(loginUser.getBlogNum() + 1);
            boolean updateUser = userService.updateById(user);
            if (!updateUser) {
                log.error("用户：{} 发布博客：{}后， 更新博客数量失败", userId, blog.getId());
            }
        }
        return blog.getId();
    }

    @Override
    public List<BlogVO> listBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
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
            BeanUtils.copyProperties(user, blogUserVO);
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
        // todo 将查询作者的文章数粉丝数，总浏览量都改为查询数据库（发布文章后，关注后，浏览后都要把数据存在数据库中）
        blogUserVO.setIsFollowed(followService.isFollowed(blogUserId, userId));
        blogVO.setBlogUserVO(blogUserVO);
        // 查询文章是否点赞、收藏
        blogVO.setIsLiked(isLiked(blogId, userId));
        blogVO.setIsStarred(isStarred(blogId, userId));
        // todo 查询文章的相关评论

        // todo 后续改为 MQ 处理
        Boolean isViewed = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_VIEW_KEY + blogId, String.valueOf(userId));
        // 增加文章浏览量（前提没浏览过），增加用户的浏览记录（此处就不用增加作者的总浏览量了，因为用户的总浏览量是所有文章浏览量之和）
        if (isViewed != null && !isViewed) {
            // 添加用户的浏览记录
            stringRedisTemplate.opsForSet().add(RedisConstant.REDIS_BLOG_VIEW_KEY + blogId, String.valueOf(userId));
            // 更新文章的浏览量啊
            Blog newBlog = new Blog();
            newBlog.setId(blog.getId());
            // todo 修复博客浏览数和用户总浏览数不 + 1 问题
            newBlog.setViewNum(blog.getViewNum() + 1);
            // 更新文章的浏览量，下次直接查询直接从数据库查
            boolean updateBlog = this.updateById(newBlog);
            if (!updateBlog) {
                log.error("用户：{} 浏览博客：{} 后，更新博客的浏览量失败了", userId, blogId);
            }
            // 更新用户总浏览量
            user = new User();
            user.setId(blogUserId);
            Long totalViewNum = baseMapper.selectObjs(new QueryWrapper<Blog>().select("SUM(viewNum)"))
                    .stream().findFirst().map(obj -> (long) obj).orElse(0L);
            user.setBlogViewNum(totalViewNum);
            boolean updateUser = userService.updateById(user);
            if (!updateUser) {
                log.error("用户：{} 浏览博客：{} 后，更新作者：{} 的总浏览量失败了", userId, blogId, blogUserId);
            }
        }
        // 不管有没有浏览过，都要增加用户的浏览记录
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
        if (starred || isStarred(blogId, userId)) {
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
        // todo 后续改为 MQ 处理
        if (count1 != null && count1 > 0 && count2 != null && count2 > 0) {
            Blog blog = new Blog();
            blog.setId(blogId);
            blog.setStarNum(count2 + 1);
            boolean updateBlog = this.updateById(blog);
            if (!updateBlog) {
                log.error("用户：{} 收藏博客：{} 后，更新博客收藏数失败了！", userId, blogId);
            }
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
        // todo 后续改为 MQ 处理
        if (count1 != null && count1 > 0 && count2 != null && count2 > 0) {
            Blog blog = new Blog();
            blog.setId(blogId);
            blog.setLikeNum(count2 + 1);
            boolean updateBlog = this.updateById(blog);
            if (!updateBlog) {
                log.error("用户：{} 点赞博客：{} 后，更新博客点赞数失败了！", userId, blogId);
            }
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
    public boolean cancelStarBlog(StarRequest starRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        boolean isStarred = starRequest.getIsStarred();
        long blogId = starRequest.getBlogId();
        if (!isStarred || this.isStarred(blogId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未收藏");
        }
        Long remove1 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        Long remove2 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        // todo 后续改为 MQ 处理
        // 更新博客的点赞数
        Blog blog = this.getById(blogId);
        Long starNum = blog.getStarNum();
        if (starNum != null && starNum > 0) {
            blog = new Blog();
            blog.setId(blogId);
            blog.setStarNum(starNum - 1);
            boolean updateBlog = this.updateById(blog);
            if (!updateBlog) {
                log.error("用户：{} 取消收藏博客：{} 后，更新博客收藏数失败了！", userId, blogId);
            }
        }
        return remove1 != null && remove1 >0 && remove2 != null && remove2 > 0;
    }

    @Override
    public boolean cancelLikeBlog(LikeRequest likeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        boolean isLiked = likeRequest.getIsLiked();
        long blogId = likeRequest.getBlogId();
        if (!isLiked || this.isLiked(blogId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未点赞");
        }
        Long remove1 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        Long remove2 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        // todo 后续改为 MQ 处理
        // 更新博客的点赞数
        Blog blog = this.getById(blogId);
        Long likeNum = blog.getLikeNum();
        if (likeNum != null && likeNum > 0) {
            blog = new Blog();
            blog.setId(blogId);
            blog.setLikeNum(likeNum - 1);
            boolean updateBlog = this.updateById(blog);
            if (!updateBlog) {
                log.error("用户：{} 取消收藏博客：{} 后，更新博客收藏数失败了！", userId, blogId);
            }
        }
        return remove1 != null && remove1 >0 && remove2 != null && remove2 > 0;
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




