package com.hjj.homieMatching.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

    /**
     * 获取和好友最后一条的聊天消息
     * @param userId
     * @param friendIdList
     * @return
     */
    List<Chat> getLastPrivateChatMessages(@Param("userId") Long userId, @Param("friendIdList") List<Long> friendIdList);

}




