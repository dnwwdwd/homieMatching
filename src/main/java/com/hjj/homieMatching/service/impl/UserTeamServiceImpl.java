package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.model.domain.UserTeam;
import com.hjj.homieMatching.service.UserTeamService;
import com.hjj.homieMatching.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 17653
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2023-12-30 15:27:32
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




