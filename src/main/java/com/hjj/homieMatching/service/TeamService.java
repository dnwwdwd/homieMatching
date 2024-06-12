package com.hjj.homieMatching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.Team;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.dto.TeamQuery;
import com.hjj.homieMatching.model.request.TeamJoinRequest;
import com.hjj.homieMatching.model.request.TeamQuitTeam;
import com.hjj.homieMatching.model.request.TeamUpdateRequest;
import com.hjj.homieMatching.model.vo.TeamUserVO;

import java.util.List;

public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍和及队伍用户
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍信息
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitTeam
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitTeam teamQuitTeam, User loginUser);

    /**
     * 解散/删除队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);

    /**
     * 我加入的队伍
     * @param loginUser
     */
    List<TeamUserVO> listMyJoinTeam(User loginUser);

    /**
     * 我创建的队伍
     * @param loginUser
     */
    List<TeamUserVO> listMyCreateTeam(User loginUser);
}
