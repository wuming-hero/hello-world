import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则需要转义字符：'$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|'
 * 异常现象： java.util.regex.PatternSyntaxException: Dangling meta. character '*' near index 0
 * 解决方法： 对特殊字符加\\转义即可。
 * <p>
 * * 0次或多次
 * + 1次或多次
 * ？0次或1次
 * {n} 刚好 n 次
 * {n,m} 从 n 到 m 次
 * <p>
 * \b 元字符是用来说明匹配单词的边界，它可以是空格或任何一种不同的标点符号(包括逗号，句号等)。
 * \d [0-9]     匹配一个数字
 * \D [^0-9]    非数字
 * \w [a-zA-Z0-9]   可以匹配一个字母或数字
 * \W [^a-zA-Z0-9]  不是字母或数字
 * \s [\t\n\r\f]    匹配一个空格（也包括Tab等空白符）
 * \S [^\t\n\r\f]   非空格
 * <p>
 * java 正则中 使用双反斜杠(\\)表示转义字符反斜杠(\)
 * <p>
 * * @author wuming
 * Created on 2017/9/27 13:59
 */
public class RegexTest {

    /**
     * 最简单的正则表达式
     * 123456
     */
    @Test
    public void simpleRegTest() {
        String inputValue = "123456777";
        Pattern pattern = Pattern.compile("123456");
        Matcher matcher = pattern.matcher(inputValue);
        System.out.println(matcher.matches());
    }

    /**
     * 最简单的手机号码验证正则表达式
     * * 1\d{10}
     */
    @Test
    public void simpleRegTest2() {
        String mobile = "13123936686";
        Pattern pattern = Pattern.compile("1\\d{10}");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
    }

    /**
     * 简单的正则表达式
     * hi
     */
    @Test
    public void simpleRegTest3() {
        String mobile = "him";
        Pattern pattern = Pattern.compile("hi");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
    }

    @Test
    public void simpleRegTest4() {
        String mobile = "10000";
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
    }

    /**
     * 匹配中汉字
     */
    @Test
    public void StringRegTest() {
        String mobile = "无名";
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
        if (matcher.matches()) {
            System.out.println(matcher.group());
        }
    }


    /*******************小试身手**********************/

    /**
     * 手机号码正则验证优化
     * 正则表达式验证手机号码是否有效
     */
    @Test
    public void mobileRegTest() {
        String mobile = "13123936686";
        Pattern pattern = Pattern.compile("1[345678]\\d{9}");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
        if (matcher.matches()) {
            System.out.println(matcher.group());
        }
    }

    /**
     * 匹配固话
     * 010 12345678
     * 05711234567
     * 0571-1234567
     * 0571 1234567
     */
    @Test
    public void phoneRegTest() {
        String phone = "05711234567";
//        phone = "0571-1234567";
//        phone = "0571 1234567";
        Pattern pattern = Pattern.compile("\\d{3,4}(-|\\s)*\\d{7,8}");
        Matcher matcher = pattern.matcher(phone);
        System.out.println(matcher.matches());
        if (matcher.matches()) {
            System.out.println(matcher.group());
        }

    }

    /**
     * iPv4的ip地址都是（1~255）.（0~255）.（0~255）.（0~255）的格式
     * 112.124.33.28
     * 127.0.0.1
     * 255.255.255.255
     * 192.168.1.1
     * 192.168.123.222
     */
    @Test
    public void ipRegTest() {
        String ip = "222.168.123.222";
        String reg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(ip);
        System.out.println(matcher.matches());
        if (matcher.matches()) {
            System.out.println(matcher.group());
        }
    }

    /**************************字符串分组及获取*************************/

    /**
     * 每行以字母 j 开头，不区分大小写
     * Pattern 知识扩展
     * 注意:只有当匹配操作成功,才可以使用start(),end(),group()三个方法,否则会抛出java.lang.IllegalStateException,
     * 也就是当matches(),lookingAt(),find()其中任意一个方法返回true时,才可以使用.
     */
    @Test
    public void groupTest() {
        String a = "java has regex\nJava has regex\n" +
                "JAVA has pretty good regular expressions\n" +
                "Regular expressions are in Java";
        Pattern p = Pattern.compile("^java", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher m = p.matcher(a);
        while (m.find()) {
            System.out.println(m.group()); // // 分组总是表示符合pattern的字符串本身
        }
    }

    /**
     * 从一个给定的字符串中找到数字串
     * group 获得字符串
     */
    @Test
    public void groupTest1() {
        // 按指定模式在字符串查找
        String line = "This order was placed for QT3000! OK?";
        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile("(\\D*)(\\d+)(.*)");
        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);
        System.out.println("groupCount: " + matcher.groupCount());
        if (matcher.find()) {
            System.out.println("group(0) value: " + matcher.group(0)); // 分组0总是表示符合pattern的字符串本身
            System.out.println("group(1) value: " + matcher.group(1)); // 第一个分组 (\D*) 匹配到的内容
            System.out.println("group(2) value: " + matcher.group(2)); // 第二个分组 (\d+) 匹配到的内容
            System.out.println("group(3) value: " + matcher.group(3)); // 第三个分组 (.*) 匹配到的内容
        } else {
            System.out.println("NO MATCH");
        }
    }

    /**
     * 给定的字符串中截取自己需要的字符串
     * 通过 group 获得字符串 oBHgGLHYvnMLxOChZeYYCENgPrZcCvxH
     */
    @Test
    public void groupTest2() {
        // 按指定模式在字符串查找
        String line = "NTES_SESS=VVEWO8vLULj7glrgYSnLplkBeEp1r_wzVi.PB7mWIFP2pyFSNmCkhA1yQ2SQCaUFZzvs4KXm_JJ3yB4aVSdJ7n" +
                "yB01SiIgMgN.9mkZ822T5B496xpvGvhIuMYZ5E24XXjQzRDRk1b3uQh2ZhXyG7HGEnf8flQVtYM61eg70EHL3oU77oRz3r1jfAW; " +
                "S_INFO=1502966524|0|2&90##|youngqiankun#kunkun0123456789#kunkun18739933735; P_INFO=youngqiankun@163.com" +
                "|1502966524|1|mail163|00&13|zhj&1502958141&mail163#zhj&330100#10#0|158155&0|mail163|youngqiankun@163.com; " +
                "NTES_PASSPORT=9Sxem5c_YQV5IpIqFp_nd8D9WBKtsX5raEUe8GljJZ6oVUXIMd7AqEwUifIi7.hXozh45181MS80NZSbm8VM." +
                "xZ8LUc3h7QVmudfLvBP36b.yzHMOjJtSjZhKixslRUQL; starttime=; Coremail.sid=vBQpwSDUicfESZzdJKUUYuzQkgtdDdPU; " +
                "mail_style=js6; mail_uid=youngqiankun@163.com; mail_host=mail.163.com; JSESSIONID=93784AEC8E5C43771ACD2EAB2E298A6B; " +
                "Province=0571; City=0571; mail_upx=t5hz.mail.163.com|t6hz.mail.163.com|t7hz.mail.163.com|t8hz.mail.163.com|" +
                "t10hz.mail.163.com|t11hz.mail.163.com|t12hz.mail.163.com|t13hz.mail.163.com|t1hz.mail.163.com|t2hz.mail.163.com|" +
                "t3hz.mail.163.com|t4hz.mail.163.com|t1bj.mail.163.com|t2bj.mail.163.com|t3bj.mail.163.com|t4bj.mail.163.com; " +
                "mail_upx_nf=; mail_idc=; Coremail=1502972936415%oBHgGLHYvnMLxOChZeYYCENgPrZcCvxH%g7a72.mail.163.com; " +
                "MAIL_MISC=youngqiankun#kunkun0123456789#kunkun18739933735; cm_last_info=dT15b3VuZ3FpYW5rdW4lNDAxNjMuY2" +
                "9tJmQ9aHR0cCUzQSUyRiUyRm1haWwuMTYzLmNvbSUyRm0lMkZtYWluLmpzcCUzRnNpZCUzRG9CSGdHTEhZdm5NTHhPQ2haZVlZQ0VOZ1By" +
                "WmNDdnhIJnM9b0JIZ0dMSFl2bk1MeE9DaFplWVlDRU5nUHJaY0N2eEgmaD1odHRwJTNBJTJGJTJGbWFpbC4xNjMuY29tJTJGbSUyRm1haW4" +
                "uanNwJTNGc2lkJTNEb0JIZ0dMSFl2bk1MeE9DaFplWVlDRU5nUHJaY0N2eEgmdz1tYWlsLjE2My5jb20mbD0wJnQ9MTE=; MAIL_SESS=" +
                "VVEWO8vLULj7glrgYSnLplkBeEp1r_wzVi.PB7mWIFP2pyFSNmCkhA1yQ2SQCaUFZzvs4KXm_JJ3yB4aVSdJ7nyB01SiIgMgN.9mkZ8" +
                "22T5B496xpvGvhIuMYZ5E24XXjQzRDRk1b3uQh2ZhXyG7HGEnf8flQVtYM61eg70EHL3oU77oRz3r1jfAW; MAIL_SINFO=1502966524" +
                "|0|2&90##|youngqiankun#kunkun0123456789#kunkun18739933735; MAIL_PINFO=youngqiankun@163.com|1502966524|1" +
                "|mail163|00&13|zhj&1502958141&mail163#zhj&330100#10#0|158155&0|mail163|youngqiankun@163.com; secu_info=1;" +
                " mail_entry_sess=99e3f971ba81b90cb605f6cdacf70461c67a4f04d6b0caf8d9389be487e9591ef648e37f524d31422cf3" +
                "dcfc33f30ad159fc7d9c68e4bffa2a2d0284f5c841c33adc68e56eab6f7b5a9366cbe86bf0185e9626fad202198ab355c8a95b09" +
                "ee93166472e6b00dadc5562ba2d7c2dae25e68c6aea266839521d2d7767b63adb143c1eec199bc96d77661b8f9f013bb57fab9e2" +
                "3b647c711141e034f39569d857925796eda7d1215f97684216ebfb5e47da58b7e82f1508efcb9519373f0411f6a6; locale=";
        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(".*%(.*)%.*");
        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);
        System.out.println("groupCount: " + matcher.groupCount());
        if (matcher.find()) {
            // System.out.println("group(0) value: " + matcher.group(0)); //分组0总是表示符合pattern的字符串本身
            System.out.println("group(1) value: " + matcher.group(1)); // 第一个分组 (\D*) 匹配到的内容
        } else {
            System.out.println("NO MATCH");
        }

        // 贪婪模式的知识点扩展
        // 默认为贪婪模式
        pattern = Pattern.compile(".*JSESSIONID=(.*);");
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("groupCount: " + matcher.groupCount());
            System.out.println("group(1) value: " + matcher.group(1));
        }
    }

    /*******************扩展***********************/

    /**
     * 字符串替换
     */
    @Test
    public void replaceTest() {
        String s = "/*! Here's a block of text to use as input to\n" +
                "    the regular expression matcher. Note that we'll\n" +
                "    first extract the block of text by looking for\n" +
                "    the special delimiters, then process the\n" +
                "    extracted block. !*/";
        // Match the specially-commented block of text above:
        Matcher matcher = Pattern.compile("/\\*!(.*)!\\*/", Pattern.DOTALL).matcher(s);
        if (matcher.find())
            s = matcher.group(1); // Captured by parentheses
        System.out.println("====s: " + s);

        // Replace two or more spaces with a single space:
        s = s.replaceAll(" {2,}", " ");
        System.out.println("----s1: " + s);

        // Replace one or more spaces at the beginning of each line with no spaces. Must enable MULTILINE mode:
        // (?m)是打开多行模式的开关，^是匹配一行的开头
        s = s.replaceAll("(?m)^ +", "");
        System.out.println("----s2: " + s);

    }


    /**
     * 匹配句子中重复的单词
     * \w 元字符用来匹配从字母a到u的任何字符
     */
    @Test
    public void repeatTest() {
        String sentence = "Paris in the the \n" +
                "spring";
        // 匹配单词
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(sentence);
        System.out.println("sentence is: " + sentence);
        while (matcher.find()) {
            System.out.println("sentence's word: " + matcher.group());
        }

        // 匹配重复单词，忽略大小写
        pattern = Pattern.compile("\\b(\\w+)\\s+\\1\\b", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(sentence);
        if (matcher.find()) {
            System.out.println("repeat word2: " + matcher.group());
        }
    }

}
