package com.hjj.homieMatching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.Message;
import org.apache.ibatis.annotations.Mapper;

/**
* @author OchiaMalu
* @description 针对表【message】的数据库操作Mapper
* @createDate 2023-06-21 17:39:30
* @Entity net.zjitc.model.domain.Message
*/
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}




