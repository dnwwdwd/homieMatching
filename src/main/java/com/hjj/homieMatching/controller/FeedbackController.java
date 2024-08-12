package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Feedback;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.FeedbackService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Resource
    private UserService userService;

    @Resource
    private FeedbackService feedbackService;

    @PostMapping("/add")
    public BaseResponse<Long> addFeedback(@RequestBody Feedback feedback, HttpServletRequest request) {
        if (feedback == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        feedback.setUserId(loginUser.getId());
        Double rate = feedback.getRate();
        String advice = feedback.getAdvice();
        if (rate == null || rate > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分不合法");
        }
        if (StringUtils.isBlank(advice) || advice.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "建议不合法");
        }
        boolean save = feedbackService.save(feedback);
        if (!save) {
            log.error("用户：{} 添加反馈失败", loginUser.getId());
        }
        return ResultUtils.success(feedback.getId());
    }

}
