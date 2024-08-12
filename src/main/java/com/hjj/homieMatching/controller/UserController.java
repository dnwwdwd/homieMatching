package com.hjj.homieMatching.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.constant.UserConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.Feedback;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.*;
import com.hjj.homieMatching.model.vo.FollowVO;
import com.hjj.homieMatching.model.vo.SignInInfoVO;
import com.hjj.homieMatching.model.vo.UserInfoVO;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(request, userRegisterRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer i = userService.userLogout(request);
        return ResultUtils.success(i);
    }

    /**
     * 获取当前用户登录信息
     *
     * @param request
     * @return safetyuser
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (userService.getLoginUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你没有管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (userService.getLoginUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody UserEditRequest userEditRequest, HttpServletRequest request) {
        // 1.校验参数是否为空
        if (userEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.校验权限
        User loginUser = userService.getLoginUser(request);

        // 3.触发更新
        int result = userService.updateUser(userEditRequest, loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/recommend")
    public BaseResponse<List<UserVO>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        if (pageSize < 0 || pageNum < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userVOS = userService.recommendUsers(pageSize, pageNum, request);
        return ResultUtils.success(userVOS);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<UserVO>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList,
                                                        HttpServletRequest request) {
        if (userService.getLoginUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userList = userService.searchUsersByTags(tagNameList, request);
        return ResultUtils.success(userList);
    }

    /**
     * 推荐最匹配的用户
     *
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<UserVO>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, loginUser));
    }

    /**
     * 搜索附近用户
     */
    @GetMapping("/searchNearby")
    public BaseResponse<List<UserVO>> searchNearby(int radius, HttpServletRequest request) {
        if (radius <= 0 || radius > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        User loginUser = userService.getById(user.getId());
        List<UserVO> userVOList = userService.searchNearby(radius, loginUser);
        return ResultUtils.success(userVOList);
    }

    @GetMapping("/blog/count")
    public BaseResponse<Long> loginUserBlogCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long count = userService.hasBlogCount(userId);
        return ResultUtils.success(count);
    }

    @GetMapping("/follower/count")
    public BaseResponse<Long> loginUserFollowerCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long count = userService.hasFollowerCount(userId);
        return ResultUtils.success(count);
    }

    @GetMapping("/info/get")
    public BaseResponse<UserInfoVO> getUserInfo(HttpServletRequest request) {
       UserInfoVO userInfoVO = userService.getUserInfo(request);
       return ResultUtils.success(userInfoVO);
    }

    @GetMapping("/score/rank")
    public BaseResponse<List<User>> getUsersScoreRank() {
        List<User> users = userService.getUsersScoreRank();
        return ResultUtils.success(users);
    }

    @PostMapping("/sign/in")
    public BaseResponse<Boolean> userSigIn(HttpServletRequest request) {
        boolean b = userService.userSigIn(request);
        return ResultUtils.success(b);
    }

    @GetMapping("/sign/in/info/get")
    public BaseResponse<SignInInfoVO> getSignedDates(HttpServletRequest request) {
        SignInInfoVO signInInfoVO = userService.getSignedDates(request);
        return ResultUtils.success(signInInfoVO);
    }

}
