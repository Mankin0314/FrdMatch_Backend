package com.dingjiaxiong.user_center_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dingjiaxiong.user_center_backend.dto.TeamQuery;
import com.dingjiaxiong.user_center_backend.model.domain.Team;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import com.dingjiaxiong.user_center_backend.model.request.TeamDeleteRequest;
import com.dingjiaxiong.user_center_backend.model.request.TeamJoinRequest;
import com.dingjiaxiong.user_center_backend.model.request.TeamQuitRequest;
import com.dingjiaxiong.user_center_backend.model.request.TeamUpdateRequest;
import com.dingjiaxiong.user_center_backend.model.vo.TeamUserVO;

import java.util.List;

/**
* @author MinJianHe
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-18 23:45:50
*/
public interface TeamService extends IService<Team> {

    //创建队伍
    long addTeam(Team team, User loginUser);

    //搜索队伍
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser);
}
