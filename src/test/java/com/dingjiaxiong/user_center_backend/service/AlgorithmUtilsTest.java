package com.dingjiaxiong.user_center_backend.service;

import com.dingjiaxiong.user_center_backend.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

public class AlgorithmUtilsTest {
    @Test
    void test(){
        LinkedList aList= new LinkedList();
        LinkedList bList = new LinkedList();
        LinkedList cList = new LinkedList();
        String a = "Mankin";
        String b = "Zizi";
        String c = "Man";
        aList.add(a+b);
        bList.add(b);
        bList.add(c);
        int i = AlgorithmUtils.minDistance(aList, bList);
        int j = AlgorithmUtils.minDistance(bList, cList);
        System.out.println(i);
    }
}
