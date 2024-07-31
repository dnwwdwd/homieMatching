package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.model.request.MessageQueryRequest;
import com.hjj.homieMatching.model.vo.InteractionMessageVO;
import com.hjj.homieMatching.service.MessageService;
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
    public BaseResponse<List<Message>> listMessages(@RequestBody MessageQueryRequest messageQueryRequest, HttpServletRequest request) {
        if (messageQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Message> messagesList = messageService.listMessages(messageQueryRequest, request);
        return ResultUtils.success(messagesList);
    }
}
