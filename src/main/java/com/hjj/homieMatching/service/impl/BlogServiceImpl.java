package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.manager.RedisBloomFilter;
import com.hjj.homieMatching.mapper.BlogMapper;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.domain.Comment;
import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogEditRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.vo.*;
import com.hjj.homieMatching.service.*;
import com.hjj.homieMatching.service.blogInteractionStrategy.BlogInteractionContext;
import com.hjj.homieMatching.service.blogInteractionStrategy.BlogInteractionStrategy;
import com.hjj.homieMatching.service.blogInteractionStrategy.impl.BlogLikedStrategy;
import com.hjj.homieMatching.service.blogInteractionStrategy.impl.BlogStarredStrategy;
import com.hjj.homieMatching.service.blogInteractionStrategy.impl.BlogViewedStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
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

    @Resource
    private MessageService messageService;

    @Resource
    private RedisBloomFilter redisBloomFilter;

    @Resource
    private CommentService commentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isBlank(coverImage) || coverImage.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "封面图片未上传或图片链接太长");
        }
        if (images.size() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片过多");
        }
        if (StringUtils.isBlank(content) || content.length() > 100000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客内容长度超过 100000");
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
            user.setScore(loginUser.getScore() + 10);
            boolean updateUser = userService.updateById(user);
            if (!updateUser) {
                log.error("用户：{} 发布博客：{}后， 更新博客数量和积分失败", userId, blog.getId());
            }
        }
        // 添加博客至博客的布隆过滤器
        redisBloomFilter.addBlogToFilter(blog.getId());
        return blog.getId();
    }

    @Override
    public List<BlogVO> listBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
        String title = blogQueryRequest.getTitle();
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(title), "title", title);
        int pageSize = blogQueryRequest.getPageSize();
        int pageNum = blogQueryRequest.getPageNum();
        long start = (pageNum - 1) * pageSize;
        long end = pageSize;
        List<Blog> blogList = this.baseMapper.selectBlogByPage(start, end, title);
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
    @Transactional(rollbackFor = Exception.class)
    public BlogVO getBlogDetailById(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        if (!redisBloomFilter.blogIsContained(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该博客不存在");
        }
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该博客不存在");
        }
        Long blogId = blog.getId();
        BlogVO blogVO = new BlogVO();
        BlogUserVO blogUserVO = new BlogUserVO();
        BeanUtils.copyProperties(blog, blogVO);
        Long blogUserId = blog.getUserId();
        User user = userService.getById(blogUserId);
        BeanUtils.copyProperties(user, blogUserVO);
        // 查询博客作者的博客数、粉丝数、总浏览量和是否关注他
        // todo 将查询作者的博客数粉丝数，总浏览量都改为查询数据库（发布博客后，关注后，浏览后都要把数据存在数据库中）
        blogUserVO.setIsFollowed(followService.isFollowed(blogUserId, userId));
        blogVO.setBlogUserVO(blogUserVO);
        // 查询博客是否点赞、收藏
        blogVO.setIsLiked(isLiked(blogId, userId));
        blogVO.setIsStarred(isStarred(blogId, userId));
        // 查询博客的相关评论
        List<CommentVO> commentVOList = commentService.listComments(blogId, request);
        blogVO.setCommentVOList(commentVOList);
        // todo 后续改为 MQ 处理
        Boolean isViewed = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_VIEW_KEY + blogId, String.valueOf(userId));
        // 增加博客浏览量（前提没浏览过），增加用户的浏览记录（此处就不用增加作者的总浏览量了，因为用户的总浏览量是所有博客浏览量之和）
        if (isViewed != null && !isViewed) {
            // 添加博客的被浏览记录
            stringRedisTemplate.opsForSet().add(RedisConstant.REDIS_BLOG_VIEW_KEY + blogId, String.valueOf(userId));
            // 更新博客的浏览量啊
            Blog newBlog = new Blog();
            newBlog.setId(blog.getId());
            newBlog.setViewNum(blog.getViewNum() + 1);
            // 更新博客的浏览量，下次直接查询直接从数据库查
            boolean updateBlog = this.updateById(newBlog);
            if (!updateBlog) {
                log.error("用户：{} 浏览博客：{} 后，更新博客的浏览量失败了", userId, blogId);
            }
            // 更新用户总浏览量
            user = new User();
            user.setId(blogUserId);
            Long totalViewNum = this.baseMapper.selectBlogTotalViewNum();
            user.setBlogViewNum(totalViewNum);
            boolean updateUser = userService.updateById(user);
            if (!updateUser) {
                log.error("用户：{} 浏览博客：{} 后，更新作者：{} 的总浏览量失败了", userId, blogId, blogUserId);
            }
        }
        // 增加用户的浏览记录
        stringRedisTemplate.opsForZSet().add(RedisConstant.REDIS_USER_VIEW_BLOG_KEY + userId,
                String.valueOf(blogId), Instant.now().toEpochMilli() / TIME_UNIT);
        return blogVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBlog(DeleteRequest deleteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long id = deleteRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客不存在");
        }
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客不存在");
        }
        if (blog.getUserId() != userId && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限删除该博客");
        }
        boolean b = this.removeById(id);
        if (!b) {
            log.error("用户：{} 删除博客 {} 失败", userId, id);
        }
        // 删除和博客相关的记录，用户关于此博客的浏览记录、点赞记录、收藏记录
        Set<String> viewedMembersId = null;
        Boolean hasKey1 = stringRedisTemplate.hasKey(RedisConstant.REDIS_BLOG_VIEW_KEY + id);
        if (hasKey1 != null && hasKey1) {
            viewedMembersId = stringRedisTemplate.opsForSet().members(RedisConstant.REDIS_BLOG_VIEW_KEY + id);
        }
        Set<String> likedMembersId = null;
        Boolean hasKey2 = stringRedisTemplate.hasKey(RedisConstant.REDIS_BLOG_LIKE_KEY + id);
        if (hasKey2 != null && hasKey2) {
            likedMembersId = stringRedisTemplate.opsForSet().members(RedisConstant.REDIS_BLOG_LIKE_KEY + id);
        }
        Set<String> starredMembersId = null;
        Boolean hasKey3 = stringRedisTemplate.hasKey(RedisConstant.REDIS_BLOG_STAR_KEY + id);
        if (hasKey3 != null && hasKey3) {
            starredMembersId = stringRedisTemplate.opsForSet().members(RedisConstant.REDIS_BLOG_STAR_KEY + id);
        }
        if (!CollectionUtils.isEmpty(viewedMembersId)) {
            for (String viewedMemberId : viewedMembersId) {
                stringRedisTemplate.opsForZSet().remove(RedisConstant.REDIS_USER_VIEW_BLOG_KEY + Long.parseLong(viewedMemberId), String.valueOf(id));
            }
        }
        if (!CollectionUtils.isEmpty(likedMembersId)) {
            for (String likedMemberId : likedMembersId) {
                stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + Long.parseLong(likedMemberId), String.valueOf(id));
            }
        }
        if (!CollectionUtils.isEmpty(starredMembersId)) {
            for (String starredMemberId : starredMembersId) {
                stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_STAR_BLOG_KEY + Long.parseLong(starredMemberId), String.valueOf(id));
            }
        }
        // 删除博客的点赞、收藏、浏览记录
        stringRedisTemplate.delete(RedisConstant.REDIS_BLOG_STAR_KEY + id);
        stringRedisTemplate.delete(RedisConstant.REDIS_BLOG_LIKE_KEY + id);
        stringRedisTemplate.delete(RedisConstant.REDIS_BLOG_VIEW_KEY + id);
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("blogId", id);
        boolean remove = commentService.remove(commentQueryWrapper);
        if (!remove) {
            log.error("用户：{} 删除博客 {} 后，删除博客的评论记录失败了", userId, id);
        }
        return b;
    }

    @Override
    public boolean starBlog(StarRequest starRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long blogId = starRequest.getBlogId();
        boolean starred = starRequest.getIsStarred();
        Blog blog = this.getById(blogId);
        // 校验参数
        if (blog == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "博客不存在");
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
            UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("starNum = starNum + 1");
            updateWrapper.eq("id", blogId);
            boolean update = this.update(updateWrapper);
            if (!update) {
                log.error("用户：{} 收藏博客：{} 后，更新博客收藏数失败了！", userId, blogId);
            }
        }
        // 添加收藏消息到消息表
        Message message = new Message();
        message.setFromId(userId);
        message.setToId(blog.getUserId());
        message.setType(0);
        message.setText("收藏了你的博客");
        message.setBlogId(blogId);
        messageService.addStarMessage(message);
        return (count1 != null && count1 >= 1) && (count2 != null && count2 >= 1);
    }

    @Override
    public boolean likeBlog(LikeRequest likeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long blogId = likeRequest.getBlogId();
        boolean liked = likeRequest.getIsLiked();
        Blog blog = this.getById(blogId);
        // 校验参数
        if (blog == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "博客不存在");
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
            UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("likeNum = likeNum + 1");
            updateWrapper.eq("id", blogId);
            boolean update = this.update(updateWrapper);
            if (!update) {
                log.error("用户：{} 点赞博客：{} 后，更新博客点赞数失败了！", userId, blogId);
            }
        }
        // 添加点赞消息到消息表
        Message message = new Message();
        message.setFromId(userId);
        message.setToId(blog.getUserId());
        message.setType(1);
        message.setText("点赞了你的博客");
        message.setBlogId(blogId);
        messageService.addLikeMessage(message);
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
        if (!isStarred || !this.isStarred(blogId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未收藏");
        }
        Long remove1 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        Long remove2 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        // todo 后续改为 MQ 处理
        // 更新博客的点赞数
        Blog blog = this.getById(blogId);
        Long starNum = blog.getStarNum();
        if (starNum != null && starNum > 0) {
            UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("starNum = starNum - 1");
            updateWrapper.eq("id", blogId);
            boolean update = this.update(updateWrapper);
            if (!update) {
                log.error("用户：{} 取消收藏博客：{} 后，更新博客收藏数失败了！", userId, blogId);
            }
        }
        return remove1 != null && remove1 > 0 && remove2 != null && remove2 > 0;
    }

    @Override
    public boolean cancelLikeBlog(LikeRequest likeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        boolean isLiked = likeRequest.getIsLiked();
        long blogId = likeRequest.getBlogId();
        if (!isLiked || !this.isLiked(blogId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未点赞");
        }
        Long remove1 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        Long remove2 = stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        // todo 后续改为 MQ 处理
        // 更新博客的点赞数
        Blog blog = this.getById(blogId);
        Long likeNum = blog.getLikeNum();
        if (likeNum != null && likeNum > 0) {
            UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("likeNum = likeNum - 1");
            updateWrapper.eq("id", blogId);
            boolean update = this.update(updateWrapper);
            if (!update) {
                log.error("用户：{} 取消收藏博客：{} 后，更新博客收藏数失败了！", userId, blogId);
            }
        }
        return remove1 != null && remove1 > 0 && remove2 != null && remove2 > 0;
    }

    @Override
    public List<BlogVO> listUserBlogs(Long id, BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        String title = blogQueryRequest.getTitle();
        int pageSize = blogQueryRequest.getPageSize();
        int pageNum = blogQueryRequest.getPageNum();
        if (!redisBloomFilter.userIsContained(id)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        QueryWrapper<Blog> queryWrapper = null;
        if (userId == id) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
            queryWrapper.eq("userId", userId);
            List<Blog> blogList = this.page(new Page<>(pageNum, pageSize), queryWrapper).getRecords();
            User user = userService.getById(userId);
            return blogList.stream().map(blog -> {
                BlogVO blogVO = new BlogVO();
                BeanUtils.copyProperties(blog, blogVO);
                BlogUserVO blogUserVO = new BlogUserVO();
                BeanUtils.copyProperties(user, blogUserVO);
                blogVO.setBlogUserVO(blogUserVO);
                return blogVO;
            }).collect(Collectors.toList());
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.eq("userId", id);
        List<Blog> blogList = this.page(new Page<>(pageNum, pageSize), queryWrapper).getRecords();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户存在");
        }
        return blogList.stream().map(blog -> {
            BlogVO blogVO = new BlogVO();
            BeanUtils.copyProperties(blog, blogVO);
            BlogUserVO blogUserVO = new BlogUserVO();
            BeanUtils.copyProperties(user, blogUserVO);
            blogVO.setBlogUserVO(blogUserVO);
            return blogVO;
        }).collect(Collectors.toList());

    }

    @Override
    public List<BlogVO> listInteractionBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
        String title = blogQueryRequest.getTitle();
        Long userId = blogQueryRequest.getUserId();
        Integer type = blogQueryRequest.getType();
        int pageSize = blogQueryRequest.getPageSize();
        int pageNum = blogQueryRequest.getPageNum();
        User loginUser = userService.getLoginUser(request);
        long loginUserId = loginUser.getId();
        if (type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Set<String> blogStringIds = null;
        // 查询谁的博客的 map
        Map<Integer, Long> blogInteractionUserMap = new LinkedHashMap<>();
        blogInteractionUserMap.put(0, loginUserId);
        blogInteractionUserMap.put(1, userId);
        // 查询博客类型的 map
        Map<Integer, BlogInteractionStrategy> blogInteractionTypeMap = new LinkedHashMap<>();
        blogInteractionTypeMap.put(0, new BlogStarredStrategy());
        blogInteractionTypeMap.put(1, new BlogLikedStrategy());
        blogInteractionTypeMap.put(2, new BlogViewedStrategy());

        BlogInteractionContext blogInteractionContext = new BlogInteractionContext(blogInteractionTypeMap.get(this.getSelectBlogType(type)));
        // 查询博客 ids
        blogStringIds = blogInteractionContext.blogInteractionMethod(blogQueryRequest, stringRedisTemplate,
                blogInteractionUserMap.get(this.getSelectUserType(type)));

        // 如果没有任何收藏或者点赞的博客 id，就直接返回空的 List
        if (CollectionUtils.isEmpty(blogStringIds)) {
            return new ArrayList<>();
        }
        List<Long> blogIds = blogStringIds.stream().map(Long::valueOf).collect(Collectors.toList());
        List<Blog> blogList = this.listByIds(blogIds);
        return blogList.stream().map(blog -> {
            BlogVO blogVO = new BlogVO();
            BeanUtils.copyProperties(blog, blogVO);
            BlogUserVO blogUserVO = new BlogUserVO();
            User user = userService.getById(blog.getUserId());
            BeanUtils.copyProperties(user, blogUserVO);
            blogVO.setBlogUserVO(blogUserVO);
            return blogVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BlogVO> listViewedBlogs(HttpServletRequest request) {
        BlogQueryRequest blogQueryRequest = new BlogQueryRequest();
        blogQueryRequest.setType(2);
        List<BlogVO> blogVOList = this.listInteractionBlogs(blogQueryRequest, request);
        return blogVOList;
    }

    @Override
    public UserBlogVO listUserInteractionBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
        Long userId = blogQueryRequest.getUserId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        User loginUser = userService.getLoginUser(request);
        long loginUserId = loginUser.getId();
        BlogUserVO blogUserVO = new BlogUserVO();
        BeanUtils.copyProperties(user, blogUserVO);
        blogUserVO.setIsFollowed(followService.isFollowed(userId, loginUserId));
        UserBlogVO userBlogVO = new UserBlogVO();
        List<BlogVO> blogVOList = null;
        if (blogQueryRequest.getType() == -1) {
            blogVOList = listUserBlogs(userId, blogQueryRequest, request);
        } else {
            blogVOList = listInteractionBlogs(blogQueryRequest, request);
        }
        userBlogVO.setBlogVOList(blogVOList);
        userBlogVO.setBlogUserVO(blogUserVO);
        return userBlogVO;
    }

    @Override
    public long editBlog(BlogEditRequest blogEditRequest, HttpServletRequest request) {
        List<String> tags = blogEditRequest.getTags();
        List<String> images = blogEditRequest.getImages();
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogEditRequest, blog);
        if (!CollectionUtils.isEmpty(tags)) {
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
            blog.setTags(tagList);
        }
        if (!CollectionUtils.isEmpty(images)) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append('[');
            for (int i = 0; i < images.size(); i++) {
                stringBuffer.append('"').append(images.get(i)).append('"');
                if (i < images.size() - 1) {
                    stringBuffer.append(',');
                }
            }
            stringBuffer.append(']');
            String imageList = stringBuffer.toString();
            blog.setImages(imageList);
        }
        boolean b = this.updateById(blog);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return blog.getId();
    }


    @Override
    public boolean isStarred(long blogId, long userId) {
        Boolean b1 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        Boolean b2 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        // 博客收藏 set 和用户收藏 set 比如都存在对方，否则删除
        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_STAR_KEY + blogId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_STAR_BLOG_KEY + userId, String.valueOf(blogId));
        }
        return b1 != null && b1 && b2 != null && b2;
    }

    @Override
    public boolean isLiked(long blogId, long userId) {
        Boolean b1 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        Boolean b2 = stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        // 博客收藏 set 和用户收藏 set 比如都存在对方，否则删除
        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet().isMember(RedisConstant.REDIS_BLOG_LIKE_KEY + blogId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet().remove(RedisConstant.REDIS_USER_LIKE_BLOG_KEY + userId, String.valueOf(blogId));
        }
        return b1 != null && b1 && b2 != null && b2;
    }

    private int getSelectUserType(int type) {
        return type / 3;
    }

    private int getSelectBlogType(int type) {
        return type % 3;
    }
}




