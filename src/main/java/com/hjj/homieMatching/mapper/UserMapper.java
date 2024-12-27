package com.hjj.homieMatching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    Long hasFollowerCount(long followeeId);

    Long hasBlogCount(long userId);

    @Select("select * from user where score > 0 order by score desc limit 10")
    List<User> selectUserTop10Score();

    @Select("select sum(likeNum) from blog where userId = ${userId} and isDelete = 0")
    Long likeBlogNum(long userId);

    @Select("select sum(starNum) from blog where userId = ${userId} and isDelete = 0")
    Long starBlogNum(long userId);
}