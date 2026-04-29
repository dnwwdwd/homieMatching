package com.hjj.homieMatching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjj.homieMatching.model.domain.StudyCheckin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudyCheckinMapper extends BaseMapper<StudyCheckin> {

    List<StudyCheckin> selectCheckinByTaskId(@Param("taskId") Long taskId,
                                               @Param("start") long start, @Param("end") long end);
}
