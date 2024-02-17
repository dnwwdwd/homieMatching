package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.FriendAddRequest;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.FriendService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/friend")
public class FriendController {

    @Resource
    UserService userService;

    @Resource
    FriendService friendService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addFriend(@RequestBody FriendAddRequest friendAddRequest, HttpServletRequest request) {
        Long friendId = friendAddRequest.getFriendId();
        if (friendId == null || friendId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        long userId = loginUser.getId();
        boolean result = friendService.addFriend(userId, friendId);
//        if (!result) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加好友失败");
//        }
        return ResultUtils.success(result);
    }

    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listFriends(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = loginUser.getId();
        List<UserVO> friendList = friendService.listFriends(userId, request);
        return ResultUtils.success(friendList);
    }
}
