package com.wuming.regex;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO 正则知识梳理及笔记
 */
public class Regex {

    @Test
    public void regTest() {
        String mobile = "15280533697";
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8]{9}/d");
        Matcher matcher = pattern.matcher(mobile);
        if (matcher.matches()) {
            System.out.println("匹配成功");
        }
    }

    @Test
    public void testB() {
        String a = " 2 ";
        String b = "2";
        Pattern pattern = Pattern.compile("\\b\\s?2\\s?\\b");
        Matcher matcher = pattern.matcher(a);
        Matcher matcher2 = pattern.matcher(b);
        if (matcher.matches()) {
            System.out.println("a匹配成功");
        } else {
            System.out.println("a匹配失败");
        }
        System.out.println(matcher2.find());
        if (matcher2.matches()) {
            System.out.println("b匹配成功");
            System.out.println(matcher2.group());
            System.out.println(matcher2.groupCount());
            System.out.println(matcher2.group(0));
            System.out.println(matcher2.find()); // 33行匹配过了 此处返回 false
            System.out.println(matcher2.find(0));
            System.out.println(matcher2.find(1));
        } else {
            System.out.println("b匹配失败");
        }
    }

    /**
     * groupCount() 是用在pattern中有'()'时使用
     */
    @Test
    public void test2() {
        String src = "sss#this#xx#that#df";
        Pattern pattern = Pattern.compile("#\\w+#");
        Pattern pattern2 = Pattern.compile("#(\\w+)#");
        Matcher matcher = pattern.matcher(src);
        Matcher matcher2 = pattern2.matcher(src);
        System.out.println("matcher.groupCount():" + matcher.groupCount());
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

        System.out.println("matcher.groupCount():" + matcher2.groupCount());
        while (matcher2.find()) {
            System.out.println(matcher2.group());
        }

    }

}