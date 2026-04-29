package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.StudyCheckinMapper;
import com.hjj.homieMatching.model.domain.StudyCheckin;
import com.hjj.homieMatching.model.domain.StudyTask;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.StudyCheckinRequest;
import com.hjj.homieMatching.model.vo.StudyCheckinVO;
import com.hjj.homieMatching.model.vo.StudyTaskUserVO;
import com.hjj.homieMatching.service.FollowService;
import com.hjj.homieMatching.service.StudyCheckinService;
import com.hjj.homieMatching.service.StudyTaskService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudyCheckinServiceImpl extends ServiceImpl<StudyCheckinMapper, StudyCheckin>
        implements StudyCheckinService {

    @Resource
    private UserService userService;

    @Resource
    private StudyTaskService studyTaskService;

    @Resource
    private FollowService followService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCheckin(StudyCheckinRequest checkinRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        Long taskId = checkinRequest.getTaskId();
        Integer actualTime = checkinRequest.getActualTime();
        String note = checkinRequest.getNote();
        List<String> images = checkinRequest.getImages();

        if (taskId == null || taskId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        StudyTask studyTask = studyTaskService.getById(taskId);
        if (studyTask == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        if (actualTime == null || actualTime < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实际学习时长不能为负数");
        }

        if (StringUtils.isNotBlank(note) && note.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "打卡备注不能超过512字符");
        }

        if (images != null && images.size() > 9) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "打卡图片不能超过9张");
        }

        StudyCheckin studyCheckin = new StudyCheckin();
        studyCheckin.setTaskId(taskId);
        studyCheckin.setUserId(userId);
        studyCheckin.setCheckinTime(new Date());
        studyCheckin.setActualTime(actualTime);
        studyCheckin.setNote(note);

        if (!CollectionUtils.isEmpty(images)) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append('[');
            for (int i = 0; i < images.size(); i++) {
                stringBuffer.append('"').append(images.get(i)).append('"');
                if (i < images.size() - 1) {
                    stringBuffer.append(',');
                }
            }
            stringBuffer.append(']');
            studyCheckin.setImages(stringBuffer.toString());
        }

        boolean save = this.save(studyCheckin);
        if (!save) {
            log.error("用户：{} 打卡失败，任务ID：{}", userId, taskId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "打卡失败");
        }

        UpdateWrapper<StudyTask> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("checkinCount = checkinCount + 1");
        updateWrapper.setSql("status = 1");
        updateWrapper.eq("id", taskId);
        boolean updateTask = studyTaskService.update(updateWrapper);
        if (!updateTask) {
            log.error("更新任务打卡次数失败，任务ID：{}", taskId);
        }

        User user = new User();
        user.setId(userId);
        user.setScore(loginUser.getScore() + 10);
        boolean updateUser = userService.updateById(user);
        if (!updateUser) {
            log.error("用户：{} 打卡后，更新积分失败", userId);
        }

        return studyCheckin.getId();
    }

    @Override
    public List<StudyCheckinVO> listCheckinsByTaskId(Long taskId, int pageSize, int pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long loginUserId = loginUser.getId();

        if (taskId == null || taskId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long start = (pageNum - 1) * pageSize;
        long end = pageSize;

        List<StudyCheckin> checkinList = this.baseMapper.selectCheckinByTaskId(taskId, start, end);

        return checkinList.stream().map(checkin -> {
            StudyCheckinVO checkinVO = new StudyCheckinVO();
            BeanUtils.copyProperties(checkin, checkinVO);

            User user = userService.getById(checkin.getUserId());
            StudyTaskUserVO checkinUserVO = new StudyTaskUserVO();
            BeanUtils.copyProperties(user, checkinUserVO);
            checkinUserVO.setIsFollowed(followService.isFollowed(checkin.getUserId(), loginUserId));
            checkinVO.setCheckinUserVO(checkinUserVO);

            return checkinVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCheckin(Long checkinId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();

        if (checkinId == null || checkinId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "打卡记录不存在");
        }

        StudyCheckin studyCheckin = this.getById(checkinId);
        if (studyCheckin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "打卡记录不存在");
        }

        if (studyCheckin.getUserId() != userId && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限删除该打卡记录");
        }

        boolean b = this.removeById(checkinId);
        if (!b) {
            log.error("用户：{} 删除打卡记录 {} 失败", userId, checkinId);
        }

        Long taskId = studyCheckin.getTaskId();
        UpdateWrapper<StudyTask> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("checkinCount = checkinCount - 1");
        updateWrapper.eq("id", taskId);
        boolean updateTask = studyTaskService.update(updateWrapper);
        if (!updateTask) {
            log.error("更新任务打卡次数失败，任务ID：{}", taskId);
        }

        return b;
    }
}
