package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.model.request.StudyCheckinRequest;
import com.hjj.homieMatching.model.request.StudyTaskAddRequest;
import com.hjj.homieMatching.model.request.StudyTaskLikeRequest;
import com.hjj.homieMatching.model.request.StudyTaskQueryRequest;
import com.hjj.homieMatching.model.vo.StudyCheckinVO;
import com.hjj.homieMatching.model.vo.StudyTaskVO;
import com.hjj.homieMatching.service.StudyCheckinService;
import com.hjj.homieMatching.service.StudyTaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/study")
public class StudyTaskController {

    @Resource
    private StudyTaskService studyTaskService;

    @Resource
    private StudyCheckinService studyCheckinService;

    @PostMapping("/task/add")
    public BaseResponse<Long> addStudyTask(@RequestBody StudyTaskAddRequest studyTaskAddRequest, HttpServletRequest request) {
        if (studyTaskAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long taskId = studyTaskService.addStudyTask(studyTaskAddRequest, request);
        return ResultUtils.success(taskId);
    }

    @PostMapping("/task/list")
    public BaseResponse<List<StudyTaskVO>> listStudyTasks(@RequestBody StudyTaskQueryRequest studyTaskQueryRequest, HttpServletRequest request) {
        if (studyTaskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (studyTaskQueryRequest.getPageNum() <= 0 || studyTaskQueryRequest.getPageSize() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<StudyTaskVO> studyTaskVOList = studyTaskService.listStudyTasks(studyTaskQueryRequest, request);
        return ResultUtils.success(studyTaskVOList);
    }

    @GetMapping("/task/get/{id}")
    public BaseResponse<StudyTaskVO> getStudyTaskById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StudyTaskVO studyTaskVO = studyTaskService.getStudyTaskById(id, request);
        return ResultUtils.success(studyTaskVO);
    }

    @PostMapping("/task/delete/{id}")
    public BaseResponse<Boolean> deleteStudyTask(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = studyTaskService.deleteStudyTask(id, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/task/like")
    public BaseResponse<Boolean> likeStudyTask(@RequestBody StudyTaskLikeRequest likeRequest, HttpServletRequest request) {
        if (likeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = studyTaskService.likeStudyTask(likeRequest, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/task/like/cancel")
    public BaseResponse<Boolean> cancelLikeStudyTask(@RequestBody StudyTaskLikeRequest likeRequest, HttpServletRequest request) {
        if (likeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = studyTaskService.cancelLikeStudyTask(likeRequest, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/task/user/{id}")
    public BaseResponse<List<StudyTaskVO>> listUserStudyTasks(@PathVariable("id") Long id,
                                                               @RequestBody StudyTaskQueryRequest studyTaskQueryRequest,
                                                               HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        List<StudyTaskVO> studyTaskVOList = studyTaskService.listUserStudyTasks(id, studyTaskQueryRequest, request);
        return ResultUtils.success(studyTaskVOList);
    }

    @PostMapping("/checkin/add")
    public BaseResponse<Long> addCheckin(@RequestBody StudyCheckinRequest checkinRequest, HttpServletRequest request) {
        if (checkinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long checkinId = studyCheckinService.addCheckin(checkinRequest, request);
        return ResultUtils.success(checkinId);
    }

    @GetMapping("/checkin/list/{taskId}")
    public BaseResponse<List<StudyCheckinVO>> listCheckinsByTaskId(@PathVariable("taskId") Long taskId,
                                                                     @RequestParam(defaultValue = "1") int pageNum,
                                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                                     HttpServletRequest request) {
        if (taskId == null || taskId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }
        List<StudyCheckinVO> checkinVOList = studyCheckinService.listCheckinsByTaskId(taskId, pageSize, pageNum, request);
        return ResultUtils.success(checkinVOList);
    }

    @PostMapping("/checkin/delete/{checkinId}")
    public BaseResponse<Boolean> deleteCheckin(@PathVariable("checkinId") Long checkinId, HttpServletRequest request) {
        if (checkinId == null || checkinId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "打卡记录不存在");
        }
        boolean result = studyCheckinService.deleteCheckin(checkinId, request);
        return ResultUtils.success(result);
    }
}
