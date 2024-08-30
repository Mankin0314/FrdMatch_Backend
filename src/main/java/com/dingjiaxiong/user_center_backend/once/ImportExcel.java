package com.dingjiaxiong.user_center_backend.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.dingjiaxiong.user_center_backend.once.TableListener;

import java.util.List;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

public class ImportExcel {

    public static void main(String[] args) {
       // readByListener();
        synchronousRead();
    }
    public static void readByListener(){
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName ="C:\\Users\\MinJianHe\\Desktop\\user_center_backend-master\\src\\main\\resources\\XingQiuTableUserInfo.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */

    public static void synchronousRead() {
        //同步读
        String fileName = "C:\\Users\\MinJianHe\\Desktop\\user_center_backend-master\\src\\main\\resources\\XingQiuTableUserInfo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        list.forEach(xingQiuTableUserInfo -> {
            System.out.println(xingQiuTableUserInfo);
        });

    }}
