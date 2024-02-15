package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.Friend;

/**
* @author 17653
* @description 针对表【friend(好友表)】的数据库操作Service
* @createDate 2024-02-15 16:45:45
*/
public interface FriendService extends IService<Friend> {

    boolean addFriend(long userId, long friendId);
}
