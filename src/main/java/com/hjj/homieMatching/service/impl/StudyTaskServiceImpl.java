package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.StudyTaskMapper;
import com.hjj.homieMatching.model.domain.Message;
import com.hjj.homieMatching.model.domain.StudyTask;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.StudyTaskAddRequest;
import com.hjj.homieMatching.model.request.StudyTaskLikeRequest;
import com.hjj.homieMatching.model.request.StudyTaskQueryRequest;
import com.hjj.homieMatching.model.vo.StudyTaskUserVO;
import com.hjj.homieMatching.model.vo.StudyTaskVO;
import com.hjj.homieMatching.service.FollowService;
import com.hjj.homieMatching.service.MessageService;
import com.hjj.homieMatching.service.StudyCheckinService;
import com.hjj.homieMatching.service.StudyTaskService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudyTaskServiceImpl extends ServiceImpl<StudyTaskMapper, StudyTask>
        implements StudyTaskService {

    @Resource
    private UserService userService;

    @Resource
    private FollowService followService;

    @Resource
    private StudyCheckinService studyCheckinService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addStudyTask(StudyTaskAddRequest studyTaskAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        String title = studyTaskAddRequest.getTitle();
        String description = studyTaskAddRequest.getDescription();
        Integer targetTime = studyTaskAddRequest.getTargetTime();
        Date taskDate = studyTaskAddRequest.getTaskDate();

        if (StringUtils.isBlank(title) || title.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务标题不能为空且不能超过128字符");
        }
        if (description != null && description.length() > 2000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务描述不能超过2000字符");
        }
        if (targetTime == null || targetTime < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标学习时长不能为负数");
        }
        if (taskDate == null) {
            taskDate = new Date();
        }

        StudyTask studyTask = new StudyTask();
        studyTask.setUserId(userId);
        studyTask.setTitle(title);
        studyTask.setDescription(description);
        studyTask.setTargetTime(targetTime);
        studyTask.setTaskDate(taskDate);
        studyTask.setStatus(0);
        studyTask.setLikeNum(0L);
        studyTask.setCheckinCount(0);

        boolean save = this.save(studyTask);
        if (!save) {
            log.error("用户：{} 创建学习任务失败", userId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建学习任务失败");
        }

        User user = new User();
        user.setId(userId);
        user.setScore(loginUser.getScore() + 5);
        boolean updateUser = userService.updateById(user);
        if (!updateUser) {
            log.error("用户：{} 发布学习任务：{}后，更新积分失败", userId, studyTask.getId());
        }

        return studyTask.getId();
    }

    @Override
    public List<StudyTaskVO> listStudyTasks(StudyTaskQueryRequest studyTaskQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long loginUserId = loginUser.getId();
        Long userId = studyTaskQueryRequest.getUserId();
        String title = studyTaskQueryRequest.getTitle();
        int pageSize = studyTaskQueryRequest.getPageSize();
        int pageNum = studyTaskQueryRequest.getPageNum();

        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long start = (pageNum - 1) * pageSize;
        long end = pageSize;

        List<StudyTask> studyTaskList = this.baseMapper.selectStudyTaskByPage(start, end, userId, title);

        return studyTaskList.stream().map(task -> {
            StudyTaskVO studyTaskVO = new StudyTaskVO();
            BeanUtils.copyProperties(task, studyTaskVO);

            User user = userService.getById(task.getUserId());
            StudyTaskUserVO taskUserVO = new StudyTaskUserVO();
            BeanUtils.copyProperties(user, taskUserVO);
            taskUserVO.setIsFollowed(followService.isFollowed(task.getUserId(), loginUserId));
            studyTaskVO.setTaskUserVO(taskUserVO);

            studyTaskVO.setIsLiked(isLiked(task.getId(), loginUserId));

            return studyTaskVO;
        }).collect(Collectors.toList());
    }

    @Override
    public StudyTaskVO getStudyTaskById(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        StudyTask studyTask = this.getById(id);
        if (studyTask == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        StudyTaskVO studyTaskVO = new StudyTaskVO();
        BeanUtils.copyProperties(studyTask, studyTaskVO);

        User user = userService.getById(studyTask.getUserId());
        StudyTaskUserVO taskUserVO = new StudyTaskUserVO();
        BeanUtils.copyProperties(user, taskUserVO);
        taskUserVO.setIsFollowed(followService.isFollowed(studyTask.getUserId(), userId));
        studyTaskVO.setTaskUserVO(taskUserVO);

        studyTaskVO.setIsLiked(isLiked(studyTask.getId(), userId));

        return studyTaskVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudyTask(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();

        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        StudyTask studyTask = this.getById(id);
        if (studyTask == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }

        if (studyTask.getUserId() != userId && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限删除该任务");
        }

        boolean b = this.removeById(id);
        if (!b) {
            log.error("用户：{} 删除学习任务 {} 失败", userId, id);
        }

        stringRedisTemplate.delete(RedisConstant.REDIS_STUDY_TASK_LIKE_KEY + id);

        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeStudyTask(StudyTaskLikeRequest likeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        long taskId = likeRequest.getTaskId();
        boolean isLiked = likeRequest.getIsLiked() != null && likeRequest.getIsLiked();

        StudyTask studyTask = this.getById(taskId);
        if (studyTask == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "任务不存在");
        }

        if (isLiked || isLiked(taskId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已点赞过");
        }

        Long count1 = stringRedisTemplate.opsForSet()
                .add(RedisConstant.REDIS_USER_LIKE_STUDY_TASK_KEY + userId, String.valueOf(taskId));
        Long count2 = stringRedisTemplate.opsForSet()
                .add(RedisConstant.REDIS_STUDY_TASK_LIKE_KEY + taskId, String.valueOf(userId));

        if (count1 == null || count1 < 1) {
            log.error("用户：{} 点赞学习任务：{} 失败了！", userId, taskId);
        }
        if (count2 == null || count2 < 1) {
            log.error("学习任务：{} 点赞添加用户：{} 失败了！", taskId, userId);
        }

        if (count1 != null && count1 > 0 && count2 != null && count2 > 0) {
            UpdateWrapper<StudyTask> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("likeNum = likeNum + 1");
            updateWrapper.eq("id", taskId);
            boolean update = this.update(updateWrapper);
            if (!update) {
                log.error("用户：{} 点赞学习任务：{} 后，更新任务点赞数失败了！", userId, taskId);
            }
        }

        Message message = new Message();
        message.setFromId(userId);
        message.setToId(studyTask.getUserId());
        message.setType(1);
        message.setText("点赞了你的学习任务");
        messageService.addLikeMessage(message);

        return (count1 != null && count1 >= 1) && (count2 != null && count2 >= 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelLikeStudyTask(StudyTaskLikeRequest likeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        boolean isLiked = likeRequest.getIsLiked() != null && likeRequest.getIsLiked();
        long taskId = likeRequest.getTaskId();

        if (!isLiked || !this.isLiked(taskId, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还未点赞");
        }

        Long remove1 = stringRedisTemplate.opsForSet()
                .remove(RedisConstant.REDIS_USER_LIKE_STUDY_TASK_KEY + userId, String.valueOf(taskId));
        Long remove2 = stringRedisTemplate.opsForSet()
                .remove(RedisConstant.REDIS_STUDY_TASK_LIKE_KEY + taskId, String.valueOf(userId));

        StudyTask studyTask = this.getById(taskId);
        Long likeNum = studyTask.getLikeNum();
        if (likeNum != null && likeNum > 0) {
            UpdateWrapper<StudyTask> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("likeNum = likeNum - 1");
            updateWrapper.eq("id", taskId);
            boolean update = this.update(updateWrapper);
            if (!update) {
                log.error("用户：{} 取消点赞学习任务：{} 后，更新任务点赞数失败了！", userId, taskId);
            }
        }

        return remove1 != null && remove1 > 0 && remove2 != null && remove2 > 0;
    }

    @Override
    public boolean isLiked(long taskId, long userId) {
        Boolean b1 = stringRedisTemplate.opsForSet()
                .isMember(RedisConstant.REDIS_USER_LIKE_STUDY_TASK_KEY + userId, String.valueOf(taskId));
        Boolean b2 = stringRedisTemplate.opsForSet()
                .isMember(RedisConstant.REDIS_STUDY_TASK_LIKE_KEY + taskId, String.valueOf(userId));

        if (b1 != null && !b1 && b2 != null && b2) {
            stringRedisTemplate.opsForSet()
                    .remove(RedisConstant.REDIS_STUDY_TASK_LIKE_KEY + taskId, String.valueOf(userId));
        }
        if (b2 != null && !b2 && b1 != null && b1) {
            stringRedisTemplate.opsForSet()
                    .remove(RedisConstant.REDIS_USER_LIKE_STUDY_TASK_KEY + userId, String.valueOf(taskId));
        }

        return b1 != null && b1 && b2 != null && b2;
    }

    @Override
    public List<StudyTaskVO> listUserStudyTasks(Long userId, StudyTaskQueryRequest studyTaskQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long loginUserId = loginUser.getId();
        String title = studyTaskQueryRequest.getTitle();
        int pageSize = studyTaskQueryRequest.getPageSize();
        int pageNum = studyTaskQueryRequest.getPageNum();

        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long start = (pageNum - 1) * pageSize;
        long end = pageSize;

        List<StudyTask> studyTaskList = this.baseMapper.selectStudyTaskByPage(start, end, userId, title);

        return studyTaskList.stream().map(task -> {
            StudyTaskVO studyTaskVO = new StudyTaskVO();
            BeanUtils.copyProperties(task, studyTaskVO);

            User user = userService.getById(task.getUserId());
            StudyTaskUserVO taskUserVO = new StudyTaskUserVO();
            BeanUtils.copyProperties(user, taskUserVO);
            taskUserVO.setIsFollowed(followService.isFollowed(task.getUserId(), loginUserId));
            studyTaskVO.setTaskUserVO(taskUserVO);

            studyTaskVO.setIsLiked(isLiked(task.getId(), loginUserId));

            return studyTaskVO;
        }).collect(Collectors.toList());
    }
}
