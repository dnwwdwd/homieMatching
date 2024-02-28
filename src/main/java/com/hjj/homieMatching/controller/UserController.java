package com.hjj.homieMatching.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.constant.UserConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.UserLoginRequest;
import com.hjj.homieMatching.model.request.UserRegisterRequest;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户接口
 * @author hjj
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode=userRegisterRequest.getPlanetCode();
        Integer gender = userRegisterRequest.getGender();
        String avatarUrl= userRegisterRequest.getAvatarUrl();
        String username= userRegisterRequest.getUsername();
        String phone= userRegisterRequest.getPhone();
        List<String> tagNameList = userRegisterRequest.getTagNameList();
        Double longitude = userRegisterRequest.getLongitude();
        Double dimension = userRegisterRequest.getDimension();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode, avatarUrl)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        if (gender == null || gender <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (phone.length() > 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号不合法");
        }
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请至少选择一个标签");
        }
        if (longitude == null || longitude > 180 || longitude < -180) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "坐标经度不合法");
        }
        if (dimension == null || dimension > 90 || dimension < -90) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "坐标维度不合法");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword,
                planetCode, gender, avatarUrl, username, phone, tagNameList, longitude, dimension);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request) {
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
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer i = userService.userLogout(request);
        return ResultUtils.success(i);
    }
    /**
     * 获取当前用户登录信息
     * @param request
     * @return safetyuser
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId=currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){
        if (userService.getLoginUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if(!userService.isAdmin(request)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"你没有管理员权限");
        }
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList= userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        if (userService.getLoginUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){
        // 1.校验参数是否为空
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.校验权限
        User loginUser = userService.getLoginUser(request);

        // 3.触发更新
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/recommend")
    public BaseResponse<List<UserVO>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        String redisKey = RedisConstant.USER_RECOMMEND_KEY + ":" + loginUser.getId();
        // 如果缓存中有数据，直接读缓存
        List<UserVO> userVOListRedis = new Gson().fromJson(stringRedisTemplate.opsForValue().get(redisKey), new TypeToken<List<UserVO>>() {
        }.getType());
        System.out.println(userVOListRedis);
        if(!CollectionUtils.isEmpty(userVOListRedis)){
           System.out.println(userVOListRedis);
           return ResultUtils.success(userVOListRedis);
       }
        // 无缓存，查询数据库，并将数据写入缓存
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", loginUser.getId());
        IPage<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userIPage = userService.page(page, queryWrapper);

        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;

        // 将User转换为UserVO
        List<UserVO> userVOList = userIPage.getRecords().stream()
                .map(user -> {
                    // 查询距离
                    Distance distance = stringRedisTemplate.opsForGeo().distance(redisUserGeoKey,
                            String.valueOf(loginUser.getId()), String.valueOf(user.getId()),
                            RedisGeoCommands.DistanceUnit.KILOMETERS);
                    double value = distance.getValue();

                    // 创建UserVO对象并设置属性
                    UserVO userVO = new UserVO();
                    userVO.setId(user.getId());
                    userVO.setUsername(user.getUsername());
                    userVO.setUserAccount(user.getUserAccount());
                    userVO.setAvatarUrl(user.getAvatarUrl());
                    userVO.setGender(user.getGender());
                    userVO.setProfile(user.getProfile());
                    userVO.setPhone(user.getPhone());
                    userVO.setEmail(user.getEmail());
                    userVO.setUserStatus(user.getUserStatus());
                    userVO.setCreateTime(user.getCreateTime());
                    userVO.setUpdateTime(user.getUpdateTime());
                    userVO.setUserRole(user.getUserRole());
                    userVO.setPlanetCode(user.getPlanetCode());
                    userVO.setTags(user.getTags());
                    userVO.setDistance(value); // 设置距离值
                    return userVO;
                })
                .collect(Collectors.toList());
        // 写缓存
       try{
           stringRedisTemplate.opsForValue().set(redisKey, new Gson().toJson(userVOList), 1, TimeUnit.MINUTES);
       } catch (Exception e) {
           log.error("redis set key error", e);
       }
        System.out.println(userVOList);
        return ResultUtils.success(userVOList);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<UserVO>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList,
                                                      HttpServletRequest request) {
        if (userService.getLoginUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if(CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userList = userService.searchUsersByTags(tagNameList, request);
        return ResultUtils.success(userList);
    }

    /**
     * 推荐最匹配的用户
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<UserVO>> matchUsers(long num, HttpServletRequest request){
        if (num <=0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num ,loginUser));
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
}
