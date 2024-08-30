package com.dingjiaxiong.user_center_backend.job;
//缓存预热任务

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingjiaxiong.user_center_backend.mapper.UserMapper;

import com.dingjiaxiong.user_center_backend.model.domain.User;
import com.dingjiaxiong.user_center_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;
    //重点用户
    List<Long> mainUserList = Arrays.asList(1L);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    //每天执行
    @Scheduled(cron ="0 19 23 * * *" )
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("FrdMatch:job:doCacheRecommendUser:lock");
        try {
            if (lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
                for(Long userid : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    // queryWrapper.select()
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey=String.format("FrdMatch:user:recommend:%s",userid);
                    try {
                        redisTemplate.opsForValue().set(redisKey,userPage,300000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("写缓存失败",e);
                    }
                }

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }
}
