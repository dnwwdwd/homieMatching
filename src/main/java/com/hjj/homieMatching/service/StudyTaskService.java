package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.StudyTask;
import com.hjj.homieMatching.model.request.StudyTaskAddRequest;
import com.hjj.homieMatching.model.request.StudyTaskLikeRequest;
import com.hjj.homieMatching.model.request.StudyTaskQueryRequest;
import com.hjj.homieMatching.model.vo.StudyTaskVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StudyTaskService extends IService<StudyTask> {

    Long addStudyTask(StudyTaskAddRequest studyTaskAddRequest, HttpServletRequest request);

    List<StudyTaskVO> listStudyTasks(StudyTaskQueryRequest studyTaskQueryRequest, HttpServletRequest request);

    StudyTaskVO getStudyTaskById(Long id, HttpServletRequest request);

    boolean deleteStudyTask(Long id, HttpServletRequest request);

    boolean likeStudyTask(StudyTaskLikeRequest likeRequest, HttpServletRequest request);

    boolean cancelLikeStudyTask(StudyTaskLikeRequest likeRequest, HttpServletRequest request);

    boolean isLiked(long taskId, long userId);

    List<StudyTaskVO> listUserStudyTasks(Long userId, StudyTaskQueryRequest studyTaskQueryRequest, HttpServletRequest request);
}
