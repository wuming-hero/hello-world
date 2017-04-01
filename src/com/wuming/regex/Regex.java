package com.wuming.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

    public static void main(String[] args) {
        String mobile = "15280533697";
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8]{9}/d");
        Matcher matcher = pattern.matcher(mobile);
        if(matcher.matches()){
            System.out.println("匹配成功");
        }
    }
}