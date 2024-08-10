package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.UserEditRequest;
import com.hjj.homieMatching.model.request.UserRegisterRequest;
import com.hjj.homieMatching.model.vo.SignInInfoVO;
import com.hjj.homieMatching.model.vo.UserInfoVO;
import com.hjj.homieMatching.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public interface UserService extends IService<User> {
    /**
     * @param userRegisterRequest
     * @return 新用户id
     */
    long userRegister(HttpServletRequest request, UserRegisterRequest userRegisterRequest);

    /**
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);

    /**
     * 根标签搜索用户，内存实现
     * @param tagNameList
     * @return
     */
    List<UserVO> searchUsersByTags(List<String> tagNameList, HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(UserEditRequest userEditRequest, User loginUser);

    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);

    List<UserVO> matchUsers(long num, User loginUser);

    List<UserVO> searchNearby(int radius, User loginUser);

    List<UserVO> recommendUsers(long pageSize, long pageNum, HttpServletRequest request);

    long hasFollowerCount(long userId);

    long hasBlogCount(long userId);

    long likeBlogNum(long userId);

    long starBlogNum(long userId);

    UserInfoVO getUserInfo(HttpServletRequest request);

    List<User> getUsersScoreRank();

    boolean userSigIn(HttpServletRequest request);

    boolean isSignedIn(HttpServletRequest request);

    SignInInfoVO getSignedDates(HttpServletRequest request);
}
