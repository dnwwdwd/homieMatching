package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.request.BlogAddRequest;
import com.hjj.homieMatching.model.request.BlogQueryRequest;
import com.hjj.homieMatching.model.request.PageRequest;
import com.hjj.homieMatching.model.vo.BlogVO;
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
    public BaseResponse<Boolean> addBlog(@RequestBody BlogAddRequest blogAddRequest, HttpServletRequest request) {
        if (blogAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = blogService.addBlog(blogAddRequest, request);
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
        BlogVO blogVO = blogService.getBlogById(id, request);
        return ResultUtils.success(blogVO);
    }

}
