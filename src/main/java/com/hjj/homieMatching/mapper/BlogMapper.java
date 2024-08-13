package com.hjj.homieMatching.mapper;

import com.hjj.homieMatching.model.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author hejiajun
* @description 针对表【blog(博客表)】的数据库操作Mapper
* @createDate 2024-07-17 16:25:17
* @Entity com.hjj.homieMatching.model.domain.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {

    @Select("select sum(viewNum) from blog")
    long selectBlogTotalViewNum();

    List<Blog> selectBlogByPage(@Param("start") long start, @Param("end") long end, @Param("title") String title);
}




