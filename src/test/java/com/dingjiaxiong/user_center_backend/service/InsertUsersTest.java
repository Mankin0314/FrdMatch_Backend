package com.dingjiaxiong.user_center_backend.service;

import com.dingjiaxiong.user_center_backend.mapper.UserMapper;
import com.dingjiaxiong.user_center_backend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;


    ExecutorService executorService= new ThreadPoolExecutor(60,1000,10000,TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

    //批量插入用户
    // @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    @Test
    public void doInsertUsers(){
        ArrayList<User> userList = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeMankin");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("1234567");
            user.setEmail("Fake123@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1111");
            user.setTags("[]");

            userList.add(user);
        }
        userService.saveBatch(userList,10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }



    @Test
    public void doConcurrentInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        //分十组
        int batchSize = 2500;
        int j =0 ;
        List<CompletableFuture<Void>> futureList =new ArrayList<>();
        for (int i = 0; i < 40 ; i++) {
            List <User> userConcurrentList = Collections.synchronizedList(new ArrayList<>());

        while(true){
            j++;
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakefakefakeMankin");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("1234567");
            user.setEmail("Fake123@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1111");
            user.setTags("[]");

            userConcurrentList.add(user);
            if (j % batchSize == 0 ){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userConcurrentList, 10000);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
