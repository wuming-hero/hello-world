package com.wuming.base;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author wuming
 * Created on 2018/4/11 11:04
 */
public class FormatTest {

    private double value = 3.14568;

    private double number = 25.00123;

    /**
     * 借助BigDecimal 进行精确计算并保留精确的小数位
     */
    @Test
    public void test() {
        // 构造BigDecimal时使用String类型的，
        BigDecimal decimal = new BigDecimal(Double.toString(value));
        System.out.println(decimal.setScale(2, BigDecimal.ROUND_HALF_UP)); // 3.15
        System.out.println(decimal.setScale(2, RoundingMode.HALF_UP)); // 3/15

        decimal = new BigDecimal(Double.toString(number));
        System.out.println(decimal.setScale(2, BigDecimal.ROUND_HALF_UP)); // 25.00
        System.out.println(decimal.setScale(2, RoundingMode.HALF_UP)); // 25.00

        // TODO wuming 2018/4/11 16:52 demo
        double val = new BigDecimal(4).divide(new BigDecimal(2), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(val); // 2.0
        System.out.println(new BigDecimal(Double.toString(val)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); // 2.0

    }

    /**
     * 使用 DecimalFormat 进行处理，且结果四舍五入
     * #.00 表示2位小数
     * #.000 表示3位小数
     * 其他类推
     */
    @Test
    public void test1() {
        DecimalFormat df = new DecimalFormat("#.00");
        System.out.println(df.format(value)); // 3.15
        System.out.println(df.format(number)); // 25.00
    }

    /**
     * 使用 NumberFormat 进行处理，且结果四舍五入
     */
    @Test
    public void test2() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2); // 为格式化对象设定小数点后的显示的最多位,显示的最后位是舍入的
        System.out.println(nf.format(value)); // 3.15
        System.out.println(nf.format(number)); // 25
    }

    /**
     * %.2f
     * %. 表示 小数点前任意位数
     * 2 表示两位小数
     * f 表示格式后的结果为浮点型
     */
    @Test
    public void test3() {
        System.out.println(String.format("%.2f", value)); // 3.15
        System.out.println(String.format("%.2f", number)); // 25.00
    }

    /****************************String.format 应用**********************/

    /**
     * 占位符格式为：%[index$][标识][最小宽度]转换符
     * <p>
     * 可用标识：
     * -， 在最小宽度内左对齐，右边用空格补上。
     * <p>
     * 可用转换符:
     * s，字符串类型。
     * c，字符类型，实参必须为char或int、short等可转换为char类型的数据类型，否则抛IllegalFormatConversionException异常。
     * b，布尔类型，只要实参为非false的布尔类型，均格式化为字符串true，否则为字符串false。
     * n，平台独立的换行符（与通过 System.getProperty("line.separator") 是一样的）
     */
    @Test
    public void stringFormat() {
        String raw = "hello";
        // 将"hello"格式化为"  hello"
        System.out.println(String.format("%1$7s", raw));
        // 简化
        System.out.println(String.format("%7s", raw));

        // 将"hello"格式化为"hello  "
        System.out.println(String.format("%1$-7s", raw));
        // 简化
        System.out.println(String.format("%-7s", raw));

        int n = 6;
        String s = "abc";
        System.out.println("%1$0" + (n - s.length()) + "d");
        System.out.println(s + String.format("%1$0" + (n - s.length()) + "d", 0));
    }

    /**
     * 占位符格式为： %[index$][标识]*[最小宽度]转换符
     * <p>
     * 可用标识：
     * -，在最小宽度内左对齐,不可以与0标识一起使用。
     * 0，若内容长度不足最小宽度，则在左边用0来填充。
     * #，对8进制和16进制，8进制前添加一个0,16进制前添加0x。
     * +，结果总包含一个+或-号。
     * 空格，正数前加空格，负数前加-号。
     * ,，只用与十进制，每3位数字间用,分隔。
     * (，若结果为负数，则用括号括住，且不显示符号。
     * <p>
     * 可用转换符：
     * b，布尔类型，只要实参为非false的布尔类型，均格式化为字符串true，否则为字符串false。
     * d，整数类型（十进制）。
     * x，整数类型（十六进制）。
     * o，整数类型（八进制）
     * n，平台独立的换行符, 也可通过System.getProperty("line.separator")获取
     */
    @Test
    public void numberTest() {
        // 将1显示为0001
        int num = 1;
        System.out.println(String.format("%04d", num));
        // 将-1000显示为(1,000)
        num = -1000;
        System.out.println(String.format("%(,d", num));
    }

    /**
     * 占位符格式为： %[index$][标识]*[最小宽度][.精度]转换符
     * <p>
     * 可用标识：
     * -，在最小宽度内左对齐,不可以与0标识一起使用。
     * 0，若内容长度不足最小宽度，则在左边用0来填充。
     * #，对8进制和16进制，8进制前添加一个0,16进制前添加0x。
     * +，结果总包含一个+或-号。
     * 空格，正数前加空格，负数前加-号。
     * ,，只用与十进制，每3位数字间用,分隔。
     * (，若结果为负数，则用括号括住，且不显示符号。
     * <p>
     * 可用转换符：
     * b，布尔类型，只要实参为非false的布尔类型，均格式化为字符串true，否则为字符串false。
     * n，平台独立的换行符, 也可通过System.getProperty("line.separator")获取。
     * f，浮点数型（十进制）。显示9位有效数字，且会进行四舍五入。如99.99。
     * a，浮点数型（十六进制）。
     * e，指数类型。如9.38e+5。
     * g，浮点数型（比%f，%a长度短些，显示6位有效数字，且会进行四舍五入）
     */
    @Test
    public void doubleTest() {
        double num = 123.4567899;
        System.out.print(String.format("a" + System.getProperty("line.separator") + "a %n")); //
        System.out.print(String.format("%f %n", num)); // 123.456790
        System.out.print(String.format("%a %n", num)); // 0x1.edd3c0bb46929p6
        System.out.print(String.format("%g %n", num)); // 123.457
    }


    /****************************DecimalFormat 应用**********************/

    /**
     * DecimalFormat 类主要靠 # 和 0 两种占位符号来指定数字长度。
     * 0 表示如果位数不足则以 0 填充，
     * # 表示一个数字，不包括 0 ，但是也得视情况而定，只要有可能就把数字拉上这个位置
     * . 小数的分隔符的占位符
     * , 分组分隔符的占位符
     * ; 分隔格式,分隔正数和负数子模式。
     * - 减号或负号，自动在数字前加上负号。
     * % 将原结果乘以 100 和作为百分比显示（人家自动乘的哟，不用你管）
     * E 分隔科学计数法中的尾数和指数。在前缀或后缀中无需加引号。
     * <p>
     * 注：如果使用具有多个分组字符的模式，则最后一个分隔符和整数结尾之间的间隔才是使用的分组大小。
     * 所以 "#,##,###,####" == "######,####" == "##,####,####"。
     * 下面的例子包含了差不多所有的基本用法
     */
    @Test
    public void decimalFormatTest() {
        double pi = 3.1415927;//圆周率
        //取整数部分
        System.out.println(new DecimalFormat("0").format(pi));//3
        //取整数部分和两位小数
        System.out.println(new DecimalFormat("0.00").format(pi));//3.14
        //取整数部分和三位小数，整数部分不足2位以0填补。
        System.out.println(new DecimalFormat("00.000").format(pi));//03.142
        //取所有整数部分
        System.out.println(new DecimalFormat("#").format(pi));//3
        //以百分比方式计数，并取两位小数
        System.out.println(new DecimalFormat("#.##%").format(pi));//314.16%

        System.out.println("-----------------------");
        long c = 299792458;//光速
        //显示为科学计数法，并取五位小数(E后面的0的个数为显示指数的位数，)
        System.out.println(new DecimalFormat("#.#####E0").format(c));//2.99792E8
        //显示为两位整数的科学计数法，并取四位小数
        System.out.println(new DecimalFormat("00.####E0").format(c));//29.9792E7
        //每三位以逗号进行分隔。
        System.out.println(new DecimalFormat(",###").format(c));//299,792,458
        //将格式嵌入文本
        System.out.println(new DecimalFormat("光速大小为每秒,###米").format(c)); //光速大小为每秒299,792,458米

        System.out.println("-----------------------");
        //格式化分隔数字
        System.out.println(new DecimalFormat("#,##,###.####").format(20000.23456)); // 20,000.2346
        System.out.println(new DecimalFormat("#.###E0").format(0.00236)); //2.36E-3

        System.out.println("-----------------------");
        System.out.println(new DecimalFormat("##0.##E0").format(12345)); // 12.345E3
        System.out.println(new DecimalFormat("#0.###E0").format(123.45)); // 1.2345E2
        System.out.println(new DecimalFormat("#0.###E0").format(0.0000123456)); // 12.346E-6
    }

}
