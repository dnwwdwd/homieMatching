package com.hjj.homieMatching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.User;

public interface UserMapper extends BaseMapper<User> {

    long hasFollowerCount(long followeeId);

    long hasBlogCount(long userId);

}