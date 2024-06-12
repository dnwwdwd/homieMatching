package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.model.domain.UserTeam;
import com.hjj.homieMatching.service.UserTeamService;
import com.hjj.homieMatching.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




