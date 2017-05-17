package com.wuming.base;

import org.junit.Test;

import java.util.Date;

/**
 * Created by wuming on 2017/5/17.
 * <p>
 * 当字符串无法确定是否具有转义字符时，而且也不需要转义时，建议使用replace函数
 * 否则，使用replaceAll函数
 */
public class Replace {

    /**
     * 在没有JAVA正则特殊字符串的情况下
     * 两者表现一致
     */
    @Test
    public void test() {
        String a = "ab43a2c43d";
        System.out.println(a.replace("3", "G"));
        System.out.println(a.replaceAll("3", "G"));
        System.out.println(a.replaceAll("\\d", "G")); // abGGaGcGGd.
        System.out.println(a.replaceFirst("\\d", "G"));
    }

    /**
     * 当使用转义字符进行替换的时候，是有区别的。
     * replaceAll的参数就是regex，是正则表达式。首先会转义，所以报错。
     */
    @Test
    public void test2() {
        // 要将里面的“kk”替换为++，可以使用两种方法得到相同的结果
        String x = "[kllkklk\\kk\\kllkk]";
        System.out.println(x.replace("kk", "++"));
        System.out.println(x.replaceAll("kk", "++"));

        // 下面将字符串中的“\\”替换为“++”
        System.out.println(x.replace("\\", "++"));    // 没有问题
//         System.out.println(x.replaceAll("\\", "++"));  //报错 illegal/unsupported escape sequence
        System.out.println(x.replaceAll("\\\\", "++"));
    }

    /**
     * 那么在使用普通的字符串替换时，选用哪一个函数呢？
     * replaceAll函数要更快一些
     */
    @Test
    public void test3() {
        String x = "[kllkklk\\kk\\kllkk]";
        String tmp;
        Long time = new Date().getTime();
        System.out.println(time);
        for (int i = 0; i < 1000000; i++) {
            tmp = x.replace("kk", "--");
        }
        Long time2 = new Date().getTime();
        System.out.println(time2 - time); // 2036
        for (int i = 0; i < 1000000; i++) {
            tmp = x.replaceAll("kk", "++");
        }
        Long time3 = new Date().getTime();
        System.out.println(time3 - time2); // 1287
    }

    /**
     * 如何将字符串中的"\"替换成"\\"：
     * '\'在java中是一个转义字符，所以需要用两个代表一个。
     * 例如System.out.println( "\\" ) ;只打印出一个"\"。
     * 但是'\'也是正则表达式中的转义字符（replaceAll 的参数就是正则表达式），需要用两个代表一个。
     * 所以：\\\\被java转换成\\,\\又被正则表达式转换成\。
     */
    @Test
    public void test4() {
        String a = "\\";
        System.out.println(a); // \
        System.out.println(a.replace("\\", "\\\\"));
        System.out.println(a.replaceAll("\\\\", "\\\\\\\\"));
    }

    /**
     * 将字符串中的'/'替换成'\'的几种方式:
     */
    @Test
    public void test5() {
        String msgIn = "/";
        System.out.println(msgIn.replaceAll("/", "\\\\"));
        System.out.println(msgIn.replace("/", "\\"));
        System.out.println(msgIn.replace('/', '\\'));
    }

    @Test
    public void test6() {
        System.out.println("\\");
        System.out.println("\\a");
        System.out.println("\\(");
    }

}
