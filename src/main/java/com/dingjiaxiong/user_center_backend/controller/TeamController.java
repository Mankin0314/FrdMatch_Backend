package com.dingjiaxiong.user_center_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingjiaxiong.user_center_backend.common.BaseResponse;
import com.dingjiaxiong.user_center_backend.common.ErrorCode;
import com.dingjiaxiong.user_center_backend.common.ResultUtils;
import com.dingjiaxiong.user_center_backend.dto.TeamQuery;
import com.dingjiaxiong.user_center_backend.exception.BusinessException;
import com.dingjiaxiong.user_center_backend.mapper.TeamMapper;
import com.dingjiaxiong.user_center_backend.mapper.UserTeamMapper;
import com.dingjiaxiong.user_center_backend.model.domain.Team;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import com.dingjiaxiong.user_center_backend.model.domain.UserTeam;
import com.dingjiaxiong.user_center_backend.model.request.*;
import com.dingjiaxiong.user_center_backend.model.vo.TeamUserVO;
import com.dingjiaxiong.user_center_backend.service.TeamService;
import com.dingjiaxiong.user_center_backend.service.UserService;
import com.dingjiaxiong.user_center_backend.service.UserTeamService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.Put;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dingjiaxiong.user_center_backend.contant.UserConstant.USER_LOGIN_STATE;

@Slf4j
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"},allowCredentials="true")
public class TeamController {
        @Resource
        private UserService userService;

        @Resource
        private TeamService teamService;

        @Resource
        private TeamMapper teamMapper;

        @Resource
        private UserTeamService userTeamService;

        @PostMapping("/add")
        private BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
            if (teamAddRequest == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
           // boolean save = teamService.save(team);
            Team team = new Team();
            BeanUtils.copyProperties(team,teamAddRequest);
            User loginUser = userService.getLoginUser(request);
            long teamId = teamService.addTeam(team, loginUser);

            return ResultUtils.success(teamId);
        }


    @PostMapping("/update")
    private BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    private BaseResponse<Team> getTeamById(long id){
         if (id <= 0){
             throw new BusinessException(ErrorCode.PARAMS_ERROR);
         }
        Team team = teamService.getById(id);
         if (team == null){
             throw new BusinessException(ErrorCode.NULL_ERROR);
         }
         return ResultUtils.success(team);
    }

/*    @GetMapping("/list")
    public BaseResponse<List<Team>> listTeams(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
//
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> list = teamService.list(queryWrapper);
      *//*  QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teams = teamMapper.selectList(queryWrapper);*//*

        return ResultUtils.success(list);
    }*/


    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);

        List<TeamUserVO> list = teamService.listTeams(teamQuery, isAdmin);
      /*  QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teams = teamMapper.selectList(queryWrapper);*/

        return ResultUtils.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        Page<Team> page =new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest httpServletRequest){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest httpServletRequest){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }


    @PostMapping("/delete")
    private BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request){
        if (teamDeleteRequest == null || teamDeleteRequest.getTeamId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamDeleteRequest,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }


    //获取我创建的队伍
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
      /*  QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teams = teamMapper.selectList(queryWrapper);*/

        return ResultUtils.success(list);
    }


    //获取我加入的队伍
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinedTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);

        //取出不重复的队伍id
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        System.out.println(listMap);
        ArrayList<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);

        List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
      /*  QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teams = teamMapper.selectList(queryWrapper);*/

        return ResultUtils.success(list);
    }


}

