package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.request.CommentAddRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.service.CommentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addComment(@RequestBody CommentAddRequest commentAddRequest,
                                            HttpServletRequest request) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = commentService.addComment(commentAddRequest, request);
        return ResultUtils.success(b);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody DeleteRequest deleteRequest,
                                               HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = commentService.deleteComment(deleteRequest, request);
        return ResultUtils.success(b);
    }

}
