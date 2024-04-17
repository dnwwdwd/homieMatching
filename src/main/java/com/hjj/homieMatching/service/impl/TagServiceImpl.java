package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.model.domain.Tag;
import com.hjj.homieMatching.service.TagService;
import com.hjj.homieMatching.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 17653
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2023-12-13 18:12:04
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




