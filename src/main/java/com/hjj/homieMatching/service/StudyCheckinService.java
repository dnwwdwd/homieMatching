package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.StudyCheckin;
import com.hjj.homieMatching.model.request.StudyCheckinRequest;
import com.hjj.homieMatching.model.vo.StudyCheckinVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StudyCheckinService extends IService<StudyCheckin> {

    Long addCheckin(StudyCheckinRequest checkinRequest, HttpServletRequest request);

    List<StudyCheckinVO> listCheckinsByTaskId(Long taskId, int pageSize, int pageNum, HttpServletRequest request);

    boolean deleteCheckin(Long checkinId, HttpServletRequest request);
}
