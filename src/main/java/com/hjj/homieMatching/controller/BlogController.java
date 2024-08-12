package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogEditRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.vo.BlogVO;
import com.hjj.homieMatching.model.vo.LikeRequest;
import com.hjj.homieMatching.model.vo.StarRequest;
import com.hjj.homieMatching.model.vo.UserBlogVO;
import com.hjj.homieMatching.service.BlogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private BlogService blogService;

    @PostMapping("/add")
    public BaseResponse<Long> addBlog(@RequestBody BlogAddRequest blogAddRequest, HttpServletRequest request) {
        if (blogAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long b = blogService.addBlog(blogAddRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/recommend")
    public BaseResponse<List<BlogVO>> listBlogs(@RequestBody BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
        if (blogQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (blogQueryRequest.getPageNum() <= 0 || blogQueryRequest.getPageSize() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<BlogVO> blogVOList = blogService.listBlogs(blogQueryRequest, request);
        return ResultUtils.success(blogVOList);
    }

    @GetMapping("/get/{id}")
    public BaseResponse<BlogVO> getBlogById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BlogVO blogVO = blogService.getBlogDetailById(id, request);
        return ResultUtils.success(blogVO);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBlog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = blogService.deleteBlog(deleteRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/star")
    public BaseResponse<Boolean> starBlog(@RequestBody StarRequest starRequest, HttpServletRequest request) {
        if (starRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = blogService.starBlog(starRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/like")
    public BaseResponse<Boolean> likeBlog(@RequestBody LikeRequest likeRequest, HttpServletRequest request) {
        if (likeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = blogService.likeBlog(likeRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/star/cancel")
    public BaseResponse<Boolean> cancelStarBlog(@RequestBody StarRequest starRequest, HttpServletRequest request) {
        if (starRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = blogService.cancelStarBlog(starRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/like/cancel")
    public BaseResponse<Boolean> cancelLikeBlog(@RequestBody LikeRequest likeRequest, HttpServletRequest request) {
        if (likeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = blogService.cancelLikeBlog(likeRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/user/{id}")
    public BaseResponse<List<BlogVO>> listUserBlogs(@PathVariable("id") Long id,
                                                    @RequestBody BlogQueryRequest blogQueryRequest, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        List<BlogVO> blogVOList = blogService.listUserBlogs(id, blogQueryRequest, request);
        return ResultUtils.success(blogVOList);
    }

    /**
     * 查询自己或其他用户的点赞、收藏、浏览过的博客
     * @param blogQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/interaction/list")
    public BaseResponse<List<BlogVO>> listLikedOrStarredBlogs(@RequestBody BlogQueryRequest blogQueryRequest,
                                                              HttpServletRequest request) {
        if (blogQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<BlogVO> blogVOList = blogService.listInteractionBlogs(blogQueryRequest, request);
        return ResultUtils.success(blogVOList);
    }

    /**
     * 查询用户的点赞、收藏、浏览过的博客
     */
    @PostMapping("/user/list")
    public BaseResponse<UserBlogVO> listUserInteractionBlogs(@RequestBody BlogQueryRequest blogQueryRequest,
                                                             HttpServletRequest request) {
        if (blogQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserBlogVO blogVOList = blogService.listUserInteractionBlogs(blogQueryRequest, request);
        return ResultUtils.success(blogVOList);
    }

    @PostMapping("/edit")
    public BaseResponse<Long> editBlog(@RequestBody BlogEditRequest blogEditRequest, HttpServletRequest request) {
        if (blogEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = blogService.editBlog(blogEditRequest, request);
        return ResultUtils.success(id);
    }

}
