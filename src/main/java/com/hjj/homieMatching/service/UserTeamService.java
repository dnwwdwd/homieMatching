package com.hjj.homieMatching.service;

import com.hjj.homieMatching.model.domain.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserTeamService extends IService<UserTeam> {

    boolean teamHasUser(long teamId, long userId);

}
