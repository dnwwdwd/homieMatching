package com.hjj.homieMatching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.StudyTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudyTaskMapper extends BaseMapper<StudyTask> {

    List<StudyTask> selectStudyTaskByPage(@Param("start") long start, @Param("end") long end,
                                            @Param("userId") Long userId, @Param("title") String title);
}
