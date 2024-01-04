package com.hjj.homieMatching.service;

import com.hjj.homieMatching.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.dto.TeamQuery;
import com.hjj.homieMatching.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 17653
* @description 针对表【team(队伍信息)】的数据库操作Service
* @createDate 2023-12-30 15:24:44
*/
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
}
