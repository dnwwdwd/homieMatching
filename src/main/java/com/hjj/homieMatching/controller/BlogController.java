package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Follow;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.request.PageRequest;
import com.hjj.homieMatching.model.vo.BlogVO;
import com.hjj.homieMatching.model.vo.FollowRequest;
import com.hjj.homieMatching.model.vo.LikeRequest;
import com.hjj.homieMatching.model.vo.StarRequest;
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

    @GetMapping("/list")
    public BaseResponse<List<BlogVO>> listBlogs(@RequestBody BlogQueryRequest blogQueryRequest) {
        if (blogQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (blogQueryRequest.getPageNum() <= 0 || blogQueryRequest.getPageSize() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<BlogVO> blogVOList = blogService.listBlogs(blogQueryRequest);
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

}
