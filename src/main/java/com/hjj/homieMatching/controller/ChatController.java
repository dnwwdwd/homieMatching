package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.constant.ChatConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.ChatRequest;
import com.hjj.homieMatching.model.vo.ChatMessageVO;
import com.hjj.homieMatching.model.vo.PrivateMessageVO;
import com.hjj.homieMatching.service.ChatService;
import com.hjj.homieMatching.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 聊天控制器
 *
 */
@RestController
@RequestMapping("/chat")
@Api(tags = "聊天管理模块")
public class ChatController {
    /**
     * 聊天服务
     */
    @Resource
    private ChatService chatService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 私聊
     *
     * @param chatRequest 聊天请求
     * @param request     请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @PostMapping("/privateChat")
    @ApiOperation(value = "获取私聊")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "chatRequest",
                    value = "聊天请求"),
                    @ApiImplicitParam(name = "request",
                            value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getPrivateChat(@RequestBody ChatRequest chatRequest,
                                                            HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> privateChat = chatService.getPrivateChat(chatRequest, ChatConstant.PRIVATE_CHAT, loginUser);
        return ResultUtils.success(privateChat);
    }

    /**
     * 团队聊天
     *
     * @param chatRequest 聊天请求
     * @param request     请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @PostMapping("/teamChat")
    @ApiOperation(value = "获取队伍聊天")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "chatRequest",
                    value = "聊天请求"),
                    @ApiImplicitParam(name = "request",
                            value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getTeamChat(@RequestBody ChatRequest chatRequest,
                                                         HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求有误");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> teamChat = chatService.getTeamChat(chatRequest, ChatConstant.TEAM_CHAT, loginUser);
        return ResultUtils.success(teamChat);
    }

    /**
     * 大厅聊天
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @GetMapping("/hallChat")
    @ApiOperation(value = "获取大厅聊天")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getHallChat(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> hallChat = chatService.getHallChat(ChatConstant.HALL_CHAT, loginUser);
        return ResultUtils.success(hallChat);
    }

    @GetMapping("/private/list")
    public BaseResponse<List<PrivateMessageVO>> listPrivateChat(HttpServletRequest request) {
        List<PrivateMessageVO> privateMessageVOList = chatService.listPrivateChat(request);
        return ResultUtils.success(privateMessageVOList);
    }
}
