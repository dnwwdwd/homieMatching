package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.PageRequest;
import com.hjj.homieMatching.model.vo.BlogVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author hejiajun
* @description 针对表【blog(博客表)】的数据库操作Service
* @createDate 2024-07-17 16:25:17
*/
public interface BlogService extends IService<Blog> {

    Boolean addBlog(BlogAddRequest blogAddRequest, HttpServletRequest request);

    List<BlogVO> listBlogs(BlogQueryRequest blogQueryRequest);

    BlogVO getBlogById(Long id, HttpServletRequest request);
}
