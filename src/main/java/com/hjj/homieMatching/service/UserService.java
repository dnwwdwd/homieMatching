package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.User;
<<<<<<< HEAD
import com.hjj.homieMatching.model.request.UserRegisterRequest;
=======
>>>>>>> 55c2d1b2d36429f4d11db279a9446ead320cb342
import com.hjj.homieMatching.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author 17653
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-10-04 13:54:07
 */
public interface UserService extends IService<User> {
    /**
<<<<<<< HEAD
     * @param userRegisterRequest
     * @return 新用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);
=======
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword ,String planetCode,
                             Integer gender, String avatarUrl, String username, String phone,
                      List<String> tagNameList, Double longitude, Double dimension);
>>>>>>> 55c2d1b2d36429f4d11db279a9446ead320cb342

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
    int updateUser(User user, User loginUser);
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
}
