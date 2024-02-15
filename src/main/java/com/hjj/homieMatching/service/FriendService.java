package com.hjj.homieMatching.service;

import com.hjj.homieMatching.model.domain.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 17653
* @description 针对表【friend(好友表)】的数据库操作Service
* @createDate 2024-02-15 16:45:45
*/
public interface FriendService extends IService<Friend> {

    boolean addFriend(long userId, long friendId);
}
