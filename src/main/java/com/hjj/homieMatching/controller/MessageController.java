package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.service.MessageService;
import com.hjj.homieMatching.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 消息控制器
 *
 * @author OchiaMalu
 * @date 2023/06/22
 */
@RestController
@RequestMapping("/message")
@Api(tags = "消息管理模块")
public class MessageController {

    /**
     * 消息服务
     */
    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    /**
     * 用户是否有新消息
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @GetMapping
    @ApiOperation(value = "用户是否有新消息")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<Boolean> userHasNewMessage(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.success(false);
        }
        Boolean hasNewMessage = messageService.hasNewMessage(loginUser.getId());
        return ResultUtils.success(hasNewMessage);
    }

    /**
     * 获取用户新消息数量
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @GetMapping("/num")
    @ApiOperation(value = "获取用户新消息数量")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<Long> getUserMessageNum(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.success(0L);
        }
        long messageNum = messageService.getMessageNum(loginUser.getId());
        return ResultUtils.success(messageNum);
    }

}
