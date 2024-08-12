package com.hjj.homieMatching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    long hasFollowerCount(long followeeId);

    long hasBlogCount(long userId);

    @Select("select * from user where score > 0 order by score desc")
    List<User> selectUserTop10Score();
}