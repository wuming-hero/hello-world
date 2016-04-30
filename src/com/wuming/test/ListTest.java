package com.wuming.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PP on 2016/4/21.
 */
public class ListTest {

    public static void main(String[] args){
        String a = "a,b,c,d";
        String[] strList = a.split(",");
        for(String s : strList){
            System.out.println(s);
        }

        for (int i = 0; i < strList.length; i++) {
            System.out.println(strList[i]);

        }

        List<String> list = Arrays.asList(strList);
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            System.out.println(s);
        }

        List<String> strList2 = new ArrayList<>();
        strList2.add("abc");
        strList2.add(1, "a");
        strList2.add("d");

    }
}
