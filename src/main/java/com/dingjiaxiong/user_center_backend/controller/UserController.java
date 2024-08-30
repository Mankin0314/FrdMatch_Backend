package com.dingjiaxiong.user_center_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingjiaxiong.user_center_backend.common.BaseResponse;
import com.dingjiaxiong.user_center_backend.common.ErrorCode;
import com.dingjiaxiong.user_center_backend.common.ResultUtils;
import com.dingjiaxiong.user_center_backend.exception.BusinessException;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import com.dingjiaxiong.user_center_backend.model.request.DeleteUserRequest;
import com.dingjiaxiong.user_center_backend.model.request.UserLoginRequest;
import com.dingjiaxiong.user_center_backend.model.request.UserRegisterRequest;
import com.dingjiaxiong.user_center_backend.model.vo.UserVO;
import com.dingjiaxiong.user_center_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dingjiaxiong.user_center_backend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author Ding Jiaxiong
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins ={"http://localhost:5173"},allowCredentials="true")
//@CrossOrigin
/**
 * 后端直接支持跨域不安全
 */
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法【比如是否已经被封号等】
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {

        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteUserRequest deleteUserRequest, HttpServletRequest request) {

        if (!userService.isAdmin(request)) {
            return null;
        }

        if (deleteUserRequest.getId() <= 0) {
            return null;
        }
        boolean b = userService.removeById(deleteUserRequest.getId());

        return ResultUtils.success(b);
    }




    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(users);

    }


    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

        //先判断缓存当中有没有,
        // 设计缓存key，用systemId:moduleId:funcId:options:
        String redisKey=String.format("FrdMatch:user:recommend:%s",loginUser.getId());
        Page<User> userPage =(Page<User>) redisTemplate.opsForValue().get(redisKey);


        if (userPage != null){
            return ResultUtils.success(userPage);
        }
        QueryWrapper<User> queryWrapper  = new QueryWrapper<>();
//        List<User> userList = userService.list(queryWrapper);
//        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
//        return ResultUtils.success(list);


        //Page<User> userList = (Page<User>) userService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>((pageNum-1)*pageSize,pageSize),queryWrapper);
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        //写入缓存
        try {
            redisTemplate.opsForValue().set(redisKey,userPage,300000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("写缓存失败",e);
        }
        return ResultUtils.success(userPage);

    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){
        //1.校验参数是否为空
        if (user ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);

    }


    //获取最匹配的用户
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <=0 || num >20 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num,loginUser));
    }


}
