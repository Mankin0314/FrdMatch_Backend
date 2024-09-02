package com.dingjiaxiong.user_center_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingjiaxiong.user_center_backend.common.ErrorCode;
import com.dingjiaxiong.user_center_backend.dto.TeamQuery;
import com.dingjiaxiong.user_center_backend.exception.BusinessException;
import com.dingjiaxiong.user_center_backend.mapper.TeamMapper;
import com.dingjiaxiong.user_center_backend.mapper.UserTeamMapper;
import com.dingjiaxiong.user_center_backend.model.domain.Team;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import com.dingjiaxiong.user_center_backend.model.domain.UserTeam;
import com.dingjiaxiong.user_center_backend.model.enums.TeamStatusEnum;
import com.dingjiaxiong.user_center_backend.model.request.TeamDeleteRequest;
import com.dingjiaxiong.user_center_backend.model.request.TeamJoinRequest;
import com.dingjiaxiong.user_center_backend.model.request.TeamQuitRequest;
import com.dingjiaxiong.user_center_backend.model.request.TeamUpdateRequest;
import com.dingjiaxiong.user_center_backend.model.vo.TeamUserVO;
import com.dingjiaxiong.user_center_backend.model.vo.UserVO;
import com.dingjiaxiong.user_center_backend.service.TeamService;

import com.dingjiaxiong.user_center_backend.service.UserService;
import com.dingjiaxiong.user_center_backend.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * @author MinJianHe
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-08-18 23:45:50
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        //校验信息
        //1.队伍人数>1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //队伍标题<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //描述《=512
        String description = team.getDescription();
        if (StringUtils.isBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
        //status是否公开？不传默认为0
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态不满足要求");
        }
        //如果status是加密状态，一定要有密码
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不符合要求");
        }


        //超时时间 >当前时间
        if (new Date().after(team.getExpireTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超前时间");
        }

        //检验用户创建了多少个队伍

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍");
        }


        //插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(loginUser.getId());
        boolean result = this.save(team);
        //teamMapper.insert(team);

        if (!result || team.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "插入队伍信息失败");
        }

        //插入用户 =>队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        boolean save = userTeamService.save(userTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }

        return team.getId();


    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null) {
                queryWrapper.eq("id", id);
            }

            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }


            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }

            String name = teamQuery.getName();
            if (name != null) {
                queryWrapper.like("name", name);
            }

            String description = teamQuery.getDescription();
            if (description != null) {
                queryWrapper.like("description", description);
            }

            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }


            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());

        }
        //不展示已过期的队伍
        //sql: expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        //List<Team> list = this.list(queryWrapper);
        List<Team> teams = teamMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(teams)) {
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOList = new ArrayList<>();

        //关联查创建人信息
        //查询队伍和创建人的信息
        //select * from team t left join user u on t.userId =u.id

        //查询队伍和已加入队伍成员的信息
        //select * from team t left join user_team ut on t.id = ut.teamId left join user u on ut.userId = u.id;


        //关联查创建人信息
        for (Team team : teams) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            UserVO userVO = new UserVO();
            if (user != null) {
                BeanUtils.copyProperties(team, teamUserVO);
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
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = teamMapper.selectById(id);
        // Team byId = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }


        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());

        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }


        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);

        boolean result = this.updateById(updateTeam);
        return result;
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        if (TeamStatusEnum.PRIVATE.equals(TeamStatusEnum.getEnumByValue(status))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私人队伍");
        }

        String password = teamJoinRequest.getPassword();

        if (TeamStatusEnum.SECRET.equals(TeamStatusEnum.getEnumByValue(status))) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }

        //该用户已加入的队伍数量
        Long id = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", id);
        // Long l = userTeamMapper.selectCount(queryWrapper);

        RLock lock = redissonClient.getLock("FrdMatch:job:JoinTeam:{teamId}}");
        try {
            while (true){
               if (lock.tryLock(0,30000, TimeUnit.MILLISECONDS)) {
                    long hasJoinNum = userTeamService.count(queryWrapper);
                    if (hasJoinNum > 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
                    }

                    //不能重复加入
                    QueryWrapper<UserTeam> hasUserJoinedWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", id);
                    queryWrapper.eq("teamId", teamId);

                    // Long l = userTeamMapper.selectCount(queryWrapper);
                    long hasUserJoinNum = userTeamService.count(queryWrapper);
                    if (hasUserJoinNum > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已在队伍");
                    }


                    //修改用户队伍表
                    //队伍已加入人数
                    long hasJoinedCount = this.countTeamUserByTeamId(teamId);
                    if (hasJoinedCount > team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    UserTeam userTeam = new UserTeam();
                    userTeam.setId(0L);
                    userTeam.setUserId(loginUser.getId());
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());

                    boolean save = userTeamService.save(userTeam);
                    return save;
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
            return false;
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);

        Long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
//        userTeam.setTeamId(teamId);
//        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        userTeamQueryWrapper.eq("userId", userId);
        Long joinedCounts = userTeamMapper.selectCount(userTeamQueryWrapper);

        if (joinedCounts == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }

        //队伍只剩一人，解散
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        if (teamHasJoinNum == 1) {
            //删除队伍和所有加入队伍的关系
            this.removeById(teamId);
//            QueryWrapper<UserTeam> userTeamQueryWrapper_Delete = new QueryWrapper<>();
//            userTeamQueryWrapper_Delete.eq("teamId",teamId);
//            return userTeamService.remove(userTeamQueryWrapper_Delete);
        } else {
            //是队长
            if (team.getUserId().equals(userId)) {
                //把队伍转移给最早加入的用户
                //1.查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper_Find = new QueryWrapper<>();
                userTeamQueryWrapper_Find.eq("teamId", teamId);
                userTeamQueryWrapper_Find.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper_Find);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
            }
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper_Delete = new QueryWrapper<>();
        userTeamQueryWrapper_Delete.eq("teamId", teamId);
        userTeamQueryWrapper_Delete.eq("userId", userId);
        return userTeamService.remove(userTeamQueryWrapper_Delete);
    }


    //删除，解散队伍
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser) {
        //校验队伍是否存在
        Long id = teamDeleteRequest.getTeamId();
        Team team = getTeamById(id);
        //校验是否为队长
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }
        //移除所有关联信息
        Long teamId = team.getId();
        System.out.println(teamId + "----------------------" + id);
        QueryWrapper<UserTeam> userTeamQueryWrapper_Delete = new QueryWrapper<>();
        userTeamQueryWrapper_Delete.eq("teamId", teamId);
        boolean resultUserTeam = userTeamService.remove(userTeamQueryWrapper_Delete);
        if (!resultUserTeam) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除关联失败");
        }
        boolean result = this.removeById(teamId);
        return result;


//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id",teamId);
//        long count = this.count(queryWrapper);
//        if (count < 1){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
//        }


    }


    //获取某队伍已加入的人数
    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        long count = userTeamService.count(userTeamQueryWrapper);
        return count;
    }


    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

}




