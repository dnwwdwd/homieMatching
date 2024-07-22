package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.request.FollowQueryRequest;
import com.hjj.homieMatching.model.vo.FollowVO;
import com.hjj.homieMatching.service.FollowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private FollowService followService;

    /**
     * 搜索关注或粉丝
     */
    @GetMapping("/list")
    public BaseResponse<List<FollowVO>> listFollows(@RequestBody FollowQueryRequest followQueryRequest,
                                                    HttpServletRequest request) {
        if (followQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<FollowVO> followVOList = followService.listFollows(followQueryRequest, request);
        return ResultUtils.success(followVOList);
    }
}
