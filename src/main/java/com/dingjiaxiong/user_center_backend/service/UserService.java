package com.dingjiaxiong.user_center_backend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import com.dingjiaxiong.user_center_backend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author DingJiaxiong
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-06-27 12:48:36
 * <p>
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    //根据标签搜索用户
    //根据标签搜索用户
    List<User> searchUsersByTags(List<String> tagList);


    Integer updateUser(User user, User loginUser);
    //仅管理员和自己可以修改

    //获取当前登录用户信息
    User getLoginUser(HttpServletRequest request);


     boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    List<User> matchUsers(long num, User loginUser);
}
