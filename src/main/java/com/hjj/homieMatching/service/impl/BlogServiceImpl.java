package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.BlogMapper;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.vo.BlogUserVO;
import com.hjj.homieMatching.model.vo.BlogVO;
import com.hjj.homieMatching.service.BlogService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Resource
    private UserService userService;

    @Override
    public Boolean addBlog(BlogAddRequest blogAddRequest, HttpServletRequest request) {
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
        if (images.size() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片过多");
        }
        if (StringUtils.isEmpty(content) || content.length() > 100000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (tags.size() > 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不得超过 4 个");
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
        return save;
    }

    @Override
    public List<BlogVO> listBlogs(BlogQueryRequest blogQueryRequest) {
        String title = blogQueryRequest.getTitle();
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(title),"title", title);
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
    public BlogVO getBlogById(Long id, HttpServletRequest request) {
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        BlogVO blogVO = new BlogVO();
        BlogUserVO blogUserVO = new BlogUserVO();
        BeanUtils.copyProperties(blog, blogVO);
        Long blogUserId = blog.getUserId();
        User user = userService.getById(blogUserId);
        BeanUtils.copyProperties(user, blogUserVO);
        blogVO.setBlogUserVO(blogUserVO);
        // todo 查询文章作者的文章数、粉丝数、总浏览量和是否关注他
        // todo 查询文章的相关评论，文章是否点赞，收藏，是否浏览过
        return blogVO;
    }
}




