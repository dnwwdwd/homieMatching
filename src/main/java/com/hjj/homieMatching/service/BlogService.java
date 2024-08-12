package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogEditRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author hejiajun
* @description 针对表【blog(博客表)】的数据库操作Service
* @createDate 2024-07-17 16:25:17
*/
public interface BlogService extends IService<Blog> {

    Long addBlog(BlogAddRequest blogAddRequest, HttpServletRequest request);

    List<BlogVO> listBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request);

    BlogVO getBlogDetailById(Long id, HttpServletRequest request);

    boolean deleteBlog(DeleteRequest deleteRequest, HttpServletRequest request);

    boolean starBlog(StarRequest starRequest, HttpServletRequest request);

    boolean likeBlog(LikeRequest likeRequest, HttpServletRequest request);

    boolean isStarred(long blogId, long userId);

    boolean isLiked(long blogId, long userId);

    int blogHasLikes(long blogId);

    int blogHasStars(long blogId);

    boolean cancelStarBlog(StarRequest starRequest, HttpServletRequest request);

    boolean cancelLikeBlog(LikeRequest likeRequest, HttpServletRequest request);

    List<BlogVO> listUserBlogs(Long id, BlogQueryRequest blogQueryRequest, HttpServletRequest request);

    List<BlogVO> listInteractionBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request);

    List<BlogVO> listViewedBlogs(HttpServletRequest request);

    UserBlogVO listUserInteractionBlogs(BlogQueryRequest blogQueryRequest, HttpServletRequest request);

    long editBlog(BlogEditRequest blogEditRequest, HttpServletRequest request);
}
