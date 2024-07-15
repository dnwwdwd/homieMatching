package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.TeamMapper;
import com.hjj.homieMatching.model.domain.Team;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.domain.UserTeam;
import com.hjj.homieMatching.model.dto.TeamQuery;
import com.hjj.homieMatching.model.enums.TeamStatusEnum;
import com.hjj.homieMatching.model.request.TeamJoinRequest;
import com.hjj.homieMatching.model.request.TeamQuitTeam;
import com.hjj.homieMatching.model.request.TeamUpdateRequest;
import com.hjj.homieMatching.model.vo.TeamUserVO;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.TeamService;
import com.hjj.homieMatching.service.UserService;
import com.hjj.homieMatching.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    UserTeamService userTeamService;

    @Resource
    UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class) // 插入到队伍表的SQL语句和插入到用户队伍表的SQL语句要么都不执行，要么都执行
    @Override
    public long addTeam(Team team, User loginUser) {
        // 1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        // 3. 校验信息
        // 1. 队伍人数 > 1，且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 2. 队伍标题 <= 20
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不符合要求");
        }
        // 3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不符合要求");
        }
        // 4. status是否公开（int）不传默认为0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        // 5. 如果status是加密状态，一定要有密码，且密码 <= 10
        if (TeamStatusEnum.SECRET.equals(statusEnum) &&
                (StringUtils.isBlank( team.getPassword()) || team.getPassword().length() > 10)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式不正确");
        }
        // 6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不能小于当前时间");
        }
        // 7. 校验用户只能创建5个队伍
        // todo 有bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        // 8. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        // 9. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper =  new QueryWrapper<>();
        // 1.组合成立条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id" ,idList);
            }
            // 搜索关键词从name字段和description两个字段里面查
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("teamName", searchText).or().like("description", searchText));
            }
            String teamName = teamQuery.getTeamName();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("teamName", teamName);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                // 查询人数相等的
                queryWrapper.eq("maxNum", maxNum);
            }
            // 根据创建人查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态查询，只查询公开的队伍
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            // statusEnum为空说明其是公开的
            if (statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        // expireTime为空或者过期时间迟于当前时间

        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            // 关联查询用户信息
            User user = userService.getById(userId);
            // 脱敏用户信息
            TeamUserVO teamUserVO = new TeamUserVO();
            if (team != null) {
                BeanUtils.copyProperties(team, teamUserVO);
            }
            UserVO userVO = new UserVO();
            if (user != null) {
                BeanUtils.copyProperties(user, userVO);
            }
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 只有管理员和队伍创建人才能更新队伍信息
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间需要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }

        String lockId = RedisConstant.USER_JOIN_TEAM + teamId;
        RLock lock = redissonClient.getLock(lockId);
        try {
            // 只有一个线程能够获取锁，并执行下面的代码
            while (true) {
                if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                    log.info(Thread.currentThread().getId() + "我拿到锁了");
                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", userId);
                    long userJoinTeamNum = userTeamService.count(queryWrapper); // 用户加入的队伍数量
                    if (userJoinTeamNum >= 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多加入5个队伍");
                    }
                    queryWrapper = new QueryWrapper<>();
                    // 不能重复加入已加入的队伍
                    queryWrapper.eq("userId", userId);
                    queryWrapper.eq("teamId", teamId);
                    long hasJoinNum = userTeamService.count(queryWrapper); // 同一个用户加入同一个队伍的数量
                    if (hasJoinNum > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经加入该队伍");
                    }
                    // 队伍中的用户数量
                    long teamHasUserNum = this.teamHasUserNum(teamId);
                    if (teamHasUserNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已满");
                    }
                    // 将用户加入队伍的信息添加到user_team表中
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("User join team error，获取分布式锁后执行逻辑异常", e);
            return  false;
        } finally { // 不管所是否会失效都会执行下段保证释放锁
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) { // 判断当前的锁是不是当前这个线程加的锁，每次抢锁时都会有一个线程Id，
                // 这个Id会存在redis中，验证线程的id就好了
                log.info(Thread.currentThread().getId() + "锁已经释放了");
                lock.unlock(); // 执行业务逻辑后，要释放锁
            }
        }
    }

    @NotNull
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitTeam teamQuitTeam, User loginUser) {
        if (teamQuitTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitTeam.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("teamId", teamId);
        long count = userTeamService.count(queryWrapper);
        if (count < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你还未加入该队伍");
        }
        long teamHasUserNum = teamHasUserNum(teamId);
        // 队伍只剩一人，解散
        if (teamHasUserNum == 1) {
            // 删除队伍
            boolean result1 = this.removeById(teamId);
        } else {
            // 队伍至少还剩两人
            // 是队长的情况
            if (team.getUserId() == userId) {
                // 把队长转移给第二早加入队伍的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId);
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队长转移失败");
                }
            }
        }
        // 移除前队长或者队员在user_team中的关系
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId", userId);
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        // 1. 校验请求参数
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 校验队伍是否存在
        Team team = getTeamById(id);
        // 3. 校验你是不是队长
        Long teamId = team.getId();
        if (team.getUserId() != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 4. 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean removeRelation = userTeamService.remove(userTeamQueryWrapper);
        if (!removeRelation) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 5. 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 查询队伍里的用户数量
     * @param teamId
     * @return
     */
    private long teamHasUserNum(long teamId) {
        // 队伍里的用户人数
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        long teamHasUserNum = userTeamService.count(queryWrapper); // 队伍里的用户人数
        return teamHasUserNum;
    }

    @Override
    public List<TeamUserVO> listMyJoinTeam(User loginUser) {
        long userId = loginUser.getId();
        User user = userService.getById(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Long> teamIdList = userTeamService.list(queryWrapper).stream().map(UserTeam::getTeamId).collect(Collectors.toList());
        List<TeamUserVO> teamUserVOList = teamIdList.stream().map(teamId -> {
            UserVO userVO = new UserVO();
            Team team = this.getById(teamId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            int hasJoinNum = (int) teamHasUserNum(teamId);
            teamUserVO.setHasJoinNum(hasJoinNum);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            // 不能重复加入已加入的队伍
            userTeamQueryWrapper.eq("userId", userId);
            userTeamQueryWrapper.eq("teamId", teamId);
            boolean hasJoin = userTeamService.count(userTeamQueryWrapper) == 1; // 同一个用户加入同一个队伍的数量
            teamUserVO.setHasJoin(hasJoin);
            return teamUserVO;
        }).collect(Collectors.toList());
        return teamUserVOList;
    }

    @Override
    public List<TeamUserVO> listMyCreateTeam(User loginUser) {
        long userId = loginUser.getId();
        User user = userService.getById(userId);
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId", userId);
        List<Team> myCreateTeamList = this.list(teamQueryWrapper);
        List<Long> teamIdList = myCreateTeamList.stream().map(Team::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        List<TeamUserVO> teamUserVOList = teamIdList.stream().map(teamId -> {
            UserVO userVO = new UserVO();
            Team team = this.getById(teamId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            int hasJoinNum = (int) teamHasUserNum(teamId);
            teamUserVO.setHasJoinNum(hasJoinNum);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            // 不能重复加入已加入的队伍
            userTeamQueryWrapper.eq("userId", userId);
            userTeamQueryWrapper.eq("teamId", teamId);
            boolean hasJoin = userTeamService.count(userTeamQueryWrapper) == 1; // 同一个用户加入同一个队伍的数量
            teamUserVO.setHasJoin(hasJoin);
            return teamUserVO;
        }).collect(Collectors.toList());
        return teamUserVOList;
    }
}
