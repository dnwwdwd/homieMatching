package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.request.MessageQueryRequest;
import com.hjj.homieMatching.model.vo.InteractionMessageVO;
import com.hjj.homieMatching.model.vo.MessageVO;
import com.hjj.homieMatching.service.MessageService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @GetMapping("/interaction/list")
    public BaseResponse<InteractionMessageVO> listInteractionMessage(HttpServletRequest request) {
        InteractionMessageVO interactionMessageVO = messageService.listInteractionMessage(request);
        return ResultUtils.success(interactionMessageVO);
    }

    @PostMapping("/list")
    public BaseResponse<List<MessageVO>> listMessages(@RequestBody MessageQueryRequest messageQueryRequest, HttpServletRequest request) {
        if (messageQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<MessageVO> messageVOList = messageService.listMessages(messageQueryRequest, request);
        return ResultUtils.success(messageVOList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMessage(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = messageService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }
}
