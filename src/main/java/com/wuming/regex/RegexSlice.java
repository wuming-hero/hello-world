package com.wuming.regex;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式贪婪与非贪婪模式
 * <p>
 * java 正则表达式截取字符串
 *
 * @author wuming
 * Created on 2020-05-14 20:17
 */
public class RegexSlice {

    /**
     * 1.什么是正则表达式的贪婪与非贪婪匹配
     * <p>
     * 如：String str="abcaxc";
     * Patter p="ab.*c";
     * 贪婪匹配:正则表达式一般趋向于最大长度匹配，也就是所谓的贪婪匹配。如上面使用模式p匹配字符串str，结果就是匹配到：abcaxc(ab.*c)。
     * 非贪婪匹配：就是匹配到结果就好，就少的匹配字符。如上面使用模式p匹配字符串str，结果就是匹配到：abc(ab.*c)。
     * <p>
     * 2.编程中如何区分两种模式
     * 默认是贪婪模式；在量词后面直接加上一个问号？就是非贪婪模式。
     * 量词：{m,n}：m到n个
     * *：任意多个
     * +：一个到多个
     * ？：0或一个
     */
    @Test
    public void test() {
        String text = "(content:\"rcpt to root\";pcre:\"word\";)";
        String rule1 = "content:\".+\"";    //贪婪模式
        String rule2 = "content:\".+?\"";    //非贪婪模式

        System.out.println("文本：" + text);
        System.out.println("贪婪模式：" + rule1);
        Pattern p1 = Pattern.compile(rule1);
        Matcher m1 = p1.matcher(text);
        while (m1.find()) {
            System.out.println("匹配结果：" + m1.group(0));
        }

        System.out.println("非贪婪模式：" + rule2);
        Pattern p2 = Pattern.compile(rule2);
        Matcher m2 = p2.matcher(text);
        while (m2.find()) {
            System.out.println("匹配结果：" + m2.group(0));
        }
    }

    /**
     * Java中用正则表达式截取字符串中第一个出现的英文左括号之前的字符串。比如：北京市(海淀区)(朝阳区)(西城区)，截取结果为：北京市。正则表达式为()
     * A   ".*?(?=\\()"
     * B   ".*?(?=\()"
     * C　　".*(?=\\()"
     * D　　".*(?=\()"
     * <p>
     * 1.什么是正则表达式的贪婪与非贪婪匹配
     * 如：String str="abcaxc";
     * <p>
     * Patter p="ab*c";
     * <p>
     * 贪婪匹配:正则表达式一般趋向于最大长度匹配，也就是所谓的贪婪匹配。如上面使用模式p匹配字符串str，结果就是匹配到：abcaxc(ab*c)。
     * <p>
     * 非贪婪匹配：就是匹配到结果就好，最少的匹配字符。如上面使用模式p匹配字符串str，结果就是匹配到：abc(ab*c)。
     * <p>
     * 2.编程中如何区分两种模式
     * <p>
     * 默认是贪婪模式；在量词后面直接加上一个问号？就是非贪婪模式。
     * 量词：
     * {m,n}：m到n个
     * *：任意多个
     * +：一个到多个
     * ？：0或一个
     * .表示除\n之外的任意字符
     * *表示匹配0-无穷
     * +表示匹配1-无穷
     * (?=Expression) 顺序环视，(?=\\()就是匹配正括号
     * <p>
     * 懒惰模式正则：".*?(?=\\())"
     * 前面的.*?是非贪婪匹配的意思， 表示找到最小的就可以了，(?=Expression) 顺序环视，(?=\\()就是匹配正括号
     * <p>
     * 解析
     * 选A， 知识点是正则表达式中的贪婪匹配。
     * 1、正则表达式中元字符:
     * "." 匹配除去\n换行符的任意字符
     * "*" 匹配前面子表达式任意次
     * "?" 匹配前面子表达式的0次或1次，如果前面也是元字符，那么它就是非贪婪匹配了(默认是贪婪匹配的)。
     * 2、B中 ".*?(?=\\()"中后面的(?=\\()它是(?=assert）的形式，叫做顺序环视，
     * 也就是前面.*?匹配到的字符后面必须要紧接着有assert中声明的值，也就是左括号(其中\\都是转义字符)，
     * 但是匹配的到的串是不包含assert中声明的内容的。
     * 3、题中，原串 “北京市(海淀区)(朝阳区)(西城区)”，首先匹配到北京市（前部分），然后北京市后面有左括号( ，这是后面顺序环视部分，
     * 但是不包括左括号，这样整个串就匹配完了，截取到的串为“北京市”。
     */
    @Test
    public void tes2() {
        String text = "北京市(海淀区)(朝阳区)(西城区)";
        Pattern pattern = Pattern.compile(".*?(?=\\()");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            System.out.println(matcher.group(0));
        }
    }

}
