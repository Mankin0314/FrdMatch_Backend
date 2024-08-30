package com.dingjiaxiong.user_center_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingjiaxiong.user_center_backend.mapper.UserMapper;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Test
    public void testAddUser() {

        User user = new User();
        user.setUsername("dogDingJiaxiong");
        user.setUserAccount("dingjiaxiong");
        user.setAvatarUrl("https://pics0.baidu.com/feed/5fdf8db1cb13495437cd2b54e0f12454d0094a7f.jpeg?token=958bb5f910b4e78dc9d5995fb46847d5");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setPhone("123");
        user.setEmail("456");

        boolean result = userService.save(user);

        System.out.println(user.getId());
        Assertions.assertTrue(result); // 希望返回true

    }

    @Test
    public void  testSearchUsersByTags(){
       // List<String> list = Arrays.asList("java", "python");
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        //queryWrapper.select("id","tags");
        queryWrapper.eq("userAccount","qiqiqi");
        userMapper.selectList(queryWrapper);

      //  List<User> users = userService.searchUsersByTags(list);
      //  Assert.assertNotNull(users);

    };

}