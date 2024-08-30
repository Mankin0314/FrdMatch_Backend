package com.dingjiaxiong.user_center_backend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        RList<String> Rlist = redissonClient.getList("test-Rlist");
        Rlist.add("yupiaaaannn");
        //System.out.println(Rlist.get(0));
       // Rlist.remove(0);
    }
}
