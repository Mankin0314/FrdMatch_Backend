package com.dingjiaxiong.user_center_backend.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//导入用户到数据库
public class ImportXingQiuUser {
    public static void main(String[] args) {
        //同步读
        String fileName = "C:\\Users\\MinJianHe\\Desktop\\user_center_backend-master\\src\\main\\resources\\XingQiuTableUserInfo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        list.forEach(xingQiuTableUserInfo -> {
            System.out.println(xingQiuTableUserInfo);
        });
        System.out.println("Total:::::"+list.size());
        Map<String, List<XingQiuTableUserInfo>> listMap = list.stream().collect(Collectors.groupingBy(XingQiuTableUserInfo::getUsername));
        System.out.println(listMap.keySet().size());


    }

}
