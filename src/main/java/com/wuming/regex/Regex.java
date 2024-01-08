package com.wuming.regex;

import lombok.experimental.var;
import org.apache.commons.lang3.StringUtils;
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
 * \s [\t\n\r\f\v]    匹配一个空格（也包括Tab等空白符）
 * \S [^\t\n\r\f\v]   非空格
 * <p>
 * java 正则中 使用双反斜杠(\\)表示转义字符反斜杠(\)
 *
 * 参考文档  https://blog.csdn.net/qq_40241957/article/details/97750036
 */
public class Regex {

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    @Test
    public void regTest() {
        String mobile = "15280533697";
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8]\\d{9}");
        Matcher matcher = pattern.matcher(mobile);
        if (matcher.matches()) {
            System.out.println("匹配成功");
        }
    }

    /**
     * (212) 555-1212
     * 212-555-1212
     * 212 555 1212
     * \s 是一个比较有用的单字符类型，用来匹配空格，比如Space键，tab键和换行符
     */
    @Test
    public void phoneNumberTest() {
        String s = "212-555-1212";
        s = "212 555 1212";
        s = "(212) 555-1212";
        Pattern pattern = Pattern.compile("\\(?\\d{3}\\)?[\\s-]\\d{3}[\\s-]\\d{4}");
        pattern = Pattern.compile("\\(?\\d{3}\\)?(-|\\s)\\d{3}(-|\\s)?\\d{4}");
        Matcher matcher = pattern.matcher(s);
        System.out.println("is match: " + matcher.matches());

        // 字符串查找
        String a = " abc(212) 555-1212,ad212-555-1212,cf212 555 1212def";
        Matcher matcher1 = pattern.matcher(a);
        while (matcher1.find()) {
            System.out.println(matcher1.group());
        }
    }

    /**
     * \b test
     * \b 元字符是用来说明匹配单词的边界，它可以是空格或任何一种不同的标点符号(包括逗号，句号等)。
     */
    @Test
    public void test() {
        String a = " 2 ";
        String b = "2";
        Pattern pattern = Pattern.compile("\\b\\s?2\\s?\\b");
        Matcher matcher = pattern.matcher(a);
        Matcher matcher2 = pattern.matcher(b);
        if (matcher.matches()) {
            // Pattern pattern = Pattern.compile("\\s?\\b2\\b\\s?"); // 这样才可以成功匹配 a
            System.out.println("a匹配成功");
        } else {
            System.out.println("a匹配失败");
        }
        System.out.println(matcher2.find());
        if (matcher2.matches()) {
            System.out.println("b匹配成功");
            System.out.println(matcher2.find()); // 33行匹配过了 此处返回 false
            System.out.println(matcher2.find(0));
            System.out.println(matcher2.find(1));
        } else {
            System.out.println("b匹配失败");
        }
    }

    /**
     * 匹配单词及句子
     * \w 元字符用来匹配从字母a到u的任何字符
     */
    @Test
    public void repeatTest() {
        String sentence = "Paris's in the the \n" +
                "spring";
        sentence = "Java has regular expressions in 1.4";
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

    /**
     * 字符串去重
     */
    @Test
    public void repeatTest2() {
        String s = "LOWVOLTAGE_LOSS_RATE,YEARMONTH,LOWVOLTAGE_LOSS_RATE,NAME,YEARMONTH,PPQ,PPQ,SPQ,PPQ,PPQ,SPQ,CODE,LOWVOLTAGE_LOSS_RATE";
        s = "D_NAME,COMPANYID,COMPANY_NAME,PARENT_ID,ID,COMPANYID,COMPANY_NAME,DF_UNIFIED_CODE,DF_CLEARANCE_TAG_ID";
        Pattern p = Pattern.compile("(\\b[\\w|_]+\\b,?)(.*)\\b\\1\\b(.*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(s);
        while (matcher.find()) {
            s = matcher.replaceAll("$1$2$3"); // 移除分组1,2,3匹配的内容
            System.out.println("====" + s);
            matcher = p.matcher(s);
        }
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }
        System.out.println("result: " + s);
    }

    /**
     * 只要字符串里有这个模式，find()就能把它给找出来，
     * 但是lookingAt( )和matches()，只有在字符串与正则表达式一开始就相匹配的情况下才能返回true。
     * matches()成功的前提是正则表达式与字符串完全匹配，
     * lookingAt()成功的前提是，字符串的开始部分与正则表达式相匹配。
     */
    @Test
    public void matcherTest() {
        String[] input = new String[]{
                "Java has regular expressions in 1.4",
                "regular expressions now expressing in Java",
                "Java represses oracular expressions"
        };
        Pattern p1 = Pattern.compile("re\\w*"),
                p2 = Pattern.compile("Java.*");
        for (int i = 0; i < input.length; i++) {
            System.out.println("input " + i + ": " + input[i]);
            Matcher m1 = p1.matcher(input[i]),
                    m2 = p2.matcher(input[i]);
            while (m1.find())
                System.out.println("m1.find() '" + m1.group() + "' start = " + m1.start() + " end = " + m1.end());
            while (m2.find())
                System.out.println("m2.find() '" + m2.group() + "' start = " + m2.start() + " end = " + m2.end());

            if (m1.lookingAt()) // No reset() necessary
                System.out.println("m1.lookingAt() start = " + m1.start() + " end = " + m1.end());
            if (m2.lookingAt())
                System.out.println("m2.lookingAt() start = " + m2.start() + " end = " + m2.end());
            if (m1.matches()) // No reset() necessary
                System.out.println("m1.matches() start = " + m1.start() + " end = " + m1.end());
            if (m2.matches())
                System.out.println("m2.matches() start = " + m2.start() + " end = " + m2.end());
            System.out.println();
        }
    }

    /**
     * groupCount() 是用在pattern中有'()'时使用
     * 可以通过调用 matcher 对象的 groupCount 方法来查看表达式有多少个分组。groupCount 方法返回一个 int 值，表示matcher对象当前有多个捕获组。
     * 还有一个特殊的组（group(0)），它总是代表整个表达式。该组不包括在 groupCount 的返回值中。
     */
    @Test
    public void groupTest1() {
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

    /**
     * 从一个给定的字符串中找到数字串
     */
    @Test
    public void groupMoreTest() {
        // 按指定模式在字符串查找
        String line = "This order was placed for QT3000! OK?";
        String pattern = "(\\D*)(\\d+)(.*)";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
            System.out.println("Found value: " + m.group(2));
            System.out.println("Found value: " + m.group(3));
        } else {
            System.out.println("NO MATCH");
        }
    }

    /**
     * 每行以字母 j 开头，不区分大小写
     */
    @Test
    public void patternTest() {
        String a = "java has regex\nJava has regex\n" +
                "JAVA has pretty good regular expressions\n" +
                "Regular expressions are in Java";
        Pattern p = Pattern.compile("^java", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher m = p.matcher(a);
        while (m.find()) {
            System.out.println(m.group());
        }
    }

    /**
     * Pattern.DOTALL 在这种模式下，表达式'.'可以匹配任意字符，包括表示一行的结束符。
     * 默认情况下，表达式'.'不匹配行的结束符。
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

        s = s.replaceFirst("[aeiou]", "(VOWEL1)");
        System.out.println("----s3: " + s);

        // appendReplacement()的使用
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("[aeiou]");
        Matcher m = p.matcher(s);
        // Process the find information as you perform the replacements:
        while (m.find()) {
            m.appendReplacement(sb, m.group().toUpperCase());
        }
        // Put in the remainder of the text:
        m.appendTail(sb);
        System.out.println("----sb: " + sb + "---" + sb.length());
    }

    /**
     * 有字符串fatcatfatcatfat,假设既有正则表达式模式为"cat"，
     * 第一次匹配后调用appendReplacement(sb,"dog"),那么这时StringBuffer sb的内容为fatdog，也就是fatcat中的cat被替换为dog并且与匹配子串前的内容加到sb里，
     * 第二次匹配后调用appendReplacement(sb,"dog")，那么sb的内容就变为fatdogfatdog，
     * 如果最后再调用一次appendTail（sb）,那么sb最终的内容将是fatdogfatdogfat
     */
    @Test
    public void appendReplacementTest() {
        //生成Pattern对象并且编译一个简单的正则表达式"Kelvin"
        Pattern p = Pattern.compile("Kelvin");
        //用Pattern类的matcher()方法生成一个Matcher对象
        Matcher m = p.matcher("Kelvin Li and Kelvin Chan are both working in Kelvin Chen's KelvinSoftShop company");
        StringBuffer sb = new StringBuffer();
        int i = 0;
        //使用find()方法查找第一个匹配的对象
        //使用循环将句子里所有的kelvin找出并替换再将内容加到sb里
        while (m.find()) {
            i++;
            m.appendReplacement(sb, "Kevin");
            System.out.println("第" + i + "次匹配后sb的内容是：" + sb);
        }
        //最后调用appendTail()方法将最后一次匹配后的剩余字符串加到sb里；
        m.appendTail(sb);
        System.out.println("调用m.appendTail(sb)后sb的最终内容是:" + sb.toString());
    }

    /**
     * 如果不给参数，reset()会把Matcher设到当前字符串的开始处。
     */
    @Test
    public void resetTest() {
        Matcher m = Pattern.compile("[frb][aiu][gx]").matcher("fix the rug with bags");
        while (m.find()) {
            System.out.println(m.group());
        }
        // 用reset()方法给现有的Matcher对象配上个新的CharSequence。
        m.reset("fix the rig with rags");
        System.out.println("-----------");
        while (m.find()) {
            System.out.println(m.group());
        }
    }

    /**
     * 正则表达式的括号的作用：
     *
     * 1. 分组和分支结构
     * 2. 捕获分组
     * 3. 反向引用
     * 4. 非捕获分组
     * 相关案例
      */

    /**
     * 分组
     *
     * 我们知道/a+/匹配连续出现的“a”，而要匹配连续出现的“ab”时，需要使用/(ab)+/。
     *
     * 其中括号是提供分组功能，使量词+作用于“ab”这个整体
     */
    // TODO manji 2024/1/8 16:17 如果匹配全部的ab?
    @Test
    public void groupTest() {
        String string = "ababa abbb ababab";
        Pattern pattern = Pattern.compile("(ab)+");
        Matcher matcher = pattern.matcher(string);
        // 输出匹配的数据
        while (matcher.find()) {
            System.out.println(matcher.group() + "----" + matcher.group(1));
        }
    }

    /**
     * 反向引用
     *
     * 除了使用相应API来引用分组，也可以在正则本身里引用分组。但只能引用之前出现的分组，即反向引用。
     */
    @Test
    public void groupMoreTest2() {
        String string = "123";
        Pattern pattern = Pattern.compile("((\\d)(\\d(\\d)))");

        Matcher matcher = pattern.matcher(string);
        System.out.println(matcher.matches());
        System.out.println("group1: " + matcher.group(1));
        System.out.println("group2: " + matcher.group(2));
        System.out.println("group3: " + matcher.group(3));
        System.out.println("group4: " + matcher.group(4));
    }


    /**
     * 分支结构
     *
     * 而在多选分支结构(p1|p2)中，此处括号的作用也是不言而喻的，提供了子表达式的所有可能。
     *
     * 如果去掉正则中的括号，即/^I love JavaScript|Regular Expression$/，匹配字符串是”I love JavaScript”和”Regular Expression”，当然这不是我们想要的。
     */
    @Test
    public void branchTest() {
        String string = "I love JavaScript";
        String string2 = "I love Regular Expression";
        Pattern pattern = Pattern.compile("I love (JavaScript|Regular Expression)");
        System.out.println( pattern.matcher(string).matches());
        System.out.println( pattern.matcher(string2).matches());
    }

    /**
     * 引用分组
     *
     * 比如提取出年、月、日
     */
    @Test
    public void groupQuoteExtraTest() {
        String string = "2017-06-12";
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        // 1. 分组数据
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            System.out.println(matcher.group());
            // group(0) = group()
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }

        // 2.引用分组数据进行替换
        System.out.println(matcher.replaceAll("$2/$3/$1"));
    }

    /**
     * 首先我们先看((\d)(\d(\d)))的意思，不管带不带括号，都是匹配3个数字。
     * 假设((\d)(\d(\d)))匹配到的3个数字是1,2,3 那么接下来我们再看
     *
     * 接下来的是\1，是第一个分组内容，那么看第一个开括号对应的分组是什么，是123，
     * 接下来的是\2，找到第2个开括号，对应的分组，匹配的内容是1，
     * 接下来的是\3，找到第3个开括号，对应的分组，匹配的内容是23，
     * 最后的是\4，找到第3个开括号，对应的分组，匹配的内容是3。
     */
    @Test
    public void groupQuoteReverseTest() {
        // (-|\/|\.)的意思是 -或/或.
        Pattern pattern = Pattern.compile("\\d{4}(-|\\/|\\.)\\d{2}(-|\\/|\\.)\\d{2}");
        System.out.println(pattern.matcher("2017-06-12").matches());
        System.out.println(pattern.matcher("2017/06/12").matches());
        System.out.println(pattern.matcher("2017.06.12").matches());
        // 错误的格式也会识别为true
        System.out.println(pattern.matcher("2017-06/12").matches());

        // 注意里面的\1，表示的引用之前的那个分组(-|\/|\.)。不管它匹配到什么（比如-），\1都匹配那个同样的具体某个字符
        Pattern reversePattern = Pattern.compile("\\d{4}(-|\\/|\\.)\\d{2}\\1\\d{2}");
        System.out.println(reversePattern.matcher("2017-06-12").matches());
        System.out.println(reversePattern.matcher("2017/06/12").matches());
        System.out.println(reversePattern.matcher("2017.06.12").matches());
        // 错误的格式正确识别，返回false
        System.out.println(reversePattern.matcher("2017-06/12").matches());
    }

    /**
     * 非捕获分组
     *
     *
     * 输入?:之后，使用\\1报错了，说明此时标注的?:表示前面的后面的都不再捕获，那么这里就不存在组号了
     */
    @Test
    public void noCatchGroupTest() {
        String string = "123122423a";
        Pattern pattern = Pattern.compile("\\d{6,20}(?:a|z)\\1");
        // 1. 分组数据
        Matcher matcher = pattern.matcher(string);
        System.out.println(matcher.matches());
        if (matcher.matches()) {
            System.out.println(matcher.group());
        }
    }

    /**
     * 正则应用：去空格
     *
     */
    @Test
    public void trimTest() {
        String string = " 123122423a ";
        // 1. 匹配到开头和结尾的空白符，然后替换成空字符
        Pattern pattern = Pattern.compile("^\\s+|\\s+$");
        Matcher matcher = pattern.matcher(string);
        System.out.println(matcher.replaceAll(""));

        // 匹配整个字符串，然后用引用来提取出相应的数据
        Pattern pattern2 = Pattern.compile("^\\s?(.*?)\\s?$");
        Matcher matcher2 = pattern2.matcher(string);
        System.out.println(matcher2.replaceAll("$1"));
    }

}