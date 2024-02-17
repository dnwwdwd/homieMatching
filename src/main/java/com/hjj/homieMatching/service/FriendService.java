package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.Friend;
import com.hjj.homieMatching.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 17653
* @description 针对表【friend(好友表)】的数据库操作Service
* @createDate 2024-02-15 16:45:45
*/
public interface FriendService extends IService<Friend> {

    boolean addFriend(long userId, long friendId);

    List<UserVO> listFriends(Long userId, HttpServletRequest request);
}
