package com.wuming.jodaTime;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Y2K （计算机系统漏洞）
 * 千禧危机，千年虫，千年问题 （千年问题可以追溯到二十世纪六十年代. 当时计算器内存非常宝贵，
 * 故而编程人员一直借助使用 MM/DD/YY 或 DD/MM/YY 即月月/日日/年年或日日/月月/年年的方式来显示年份，
 * 但是当年序来到公元2000年的1月1日，系统却无法自动辨识00/01/01究竟代表1900年的1月1日，还是2000年的1月1日，
 * 所有的软硬件都可能因为日期的混淆而产生资料流失、系统死机、程序紊乱、控制失灵等问题，如此所造成的损失以及灾难是无法估计想像的.
 *
 * @author wuming
 * Created on 2017/11/17 20:33
 */
public class JodaTimeTest {

    /**
     * 使用JDK操作时间与Joda操作时间对比
     * 1.创建一个用时间表示的某个随意的时刻 — 比如，2000 年 1 月 1 日 0 时 0 分
     * 2.时间加减操作
     */
    @Test
    public void jdkVsJoda() {
        // 1.JDK 实现方式 借助Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        // 时间加减操作
        SimpleDateFormat sdf = new SimpleDateFormat("E MM/dd/yyyy HH:mm:ss.SSS");
        calendar.add(Calendar.DAY_OF_MONTH, 90);
        System.out.println(sdf.format(calendar.getTime()));

        // Joda 直接提供构造函数
        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);
        System.out.println(dateTime.plusDays(90).toString("E MM/dd/yyyy HH:mm:ss.SSS"));
    }

    /**
     * 构建DateTime对象
     * 所有的构建DateTime方法中，默认使用UTC时间
     */
    @Test
    public void createDateTimeTest() {
        // 通过时间戳构建
        DateTime dateTime = new DateTime(new Date().getTime());

        // 通过日历构建
        dateTime = new DateTime(Calendar.getInstance());

        // 年,月,日,时,分,秒,毫秒 类似的构建方法有很多
        dateTime = new DateTime(2015, 12, 21, 0, 0, 0, 333);

        // 通过字符串构建 年月日的字符串 和 UTC字符串 可以直接转换
        dateTime = new DateTime("2015-12-21");
        dateTime = new DateTime("2006-01-26T13:30:00-06:00");

        // 2015-12-21 23:22:45字符串 必须对其进行精确地格式化
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        dateTime = DateTime.parse("2015-12-21 23:22:45", format);

        // 格式化输出
        System.out.println(dateTime.toString("yyyy/MM/dd HH:mm:ss EE"));
        // 将字符串解析为DateTime对象
        System.out.println(dateTime.toString("yyyy/MM/dd HH:mm:ss EE"));

        // 通过dateTime实例构建
        DateTime dateTime1 = new DateTime(dateTime);
    }

    /**
     * 时间操作处理
     * Joda 中常用的属性（property），他们是计算威力的关键
     * yearOfCentury
     * dayOfYear
     * monthOfYear
     * dayOfMonth
     * dayOfWeek
     */
    @Test
    public void timeHandleTest() {
        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);
        // 某个日期上加上90天
        System.out.println(dateTime.plusDays(90).toString("E MM/dd/yyyy HH:mm:ss.SSS"));
        // 距离 Y2K 45 天之后的某天在下一个月的当前周的最后一天的日期
        System.out.println(dateTime.plusDays(45).plusMonths(1).dayOfWeek().withMaximumValue().toString("E MM/dd/yyyy HH:mm:ss.SSS"));
        // 计算五年后的第二个月的最后一天
        DateTime then = dateTime.plusYears(5) // five years ago
                .monthOfYear()     // get monthOfYear property
                .setCopy(2)        // set it to February
                .dayOfMonth()      // get dayOfMonth property
                .withMaximumValue();// the last day of the month
        System.out.println("then: " + then);

        // LocalDate 操作 和DateTime基本一致
        LocalDate now = dateTime.toLocalDate();
        // 计算上一个月的最后一天
        LocalDate lastDayOfPreviousMonth = now.minusMonths(1).dayOfMonth().withMaximumValue();
        System.out.println("lastDayOfPreviousMonth: " + lastDayOfPreviousMonth);

        // 计算 11 月中第一个星期一之后的第一个星期二
        /**
         * .setCopy("Monday") 是整个计算的关键。不管中间 LocalDate 值是多少，
         * 将其 dayOfWeek 属性设置为 Monday 总是能够四舍五入，这样的话，
         * 在每月的开始再加上 6 天就能够让您得到第一个星期一。再加上一天就得到第一个星期二
         */
        LocalDate electionDate = now.monthOfYear()
                .setCopy(11)        // November
                .dayOfMonth()       // Access Day Of Month Property
                .withMinimumValue() // Get its minimum value
                .plusDays(6)        // Add 6 days
                .dayOfWeek()        // Access Day Of Week Property
                .setCopy("1")  // Set to Monday (it will round down)
                .plusDays(1);       // Gives us Tuesday
        System.out.println("electionDate: " + electionDate);

        LocalDate electionDate2 = now.monthOfYear()
                .setCopy(11)        // November
                .dayOfMonth()       // Access Day Of Month Property
                .withMinimumValue() // Get its minimum value
                .plusDays(6)        // Add 6 days
                .withDayOfWeek(DateTimeConstants.MONDAY)  // Set to Monday (it will round down)
                .plusDays(1);       // Gives us Tuesday
        System.out.println("electionDate2: " + electionDate2);

    }

    /**
     * 计算间隔和区间
     */
    @Test
    public void durationTest() {
        DateTime begin = new DateTime("2015-02-01");
        DateTime end = new DateTime("2016-05-01");

        //计算时间段毫秒数
        Duration d = new Duration(begin, end);
        long millis = d.getMillis();
        System.out.println("时间间隔：" + millis);
        System.out.println("时间间隔天数：" + d.getStandardDays());

        //计算区间天数
        Period p = new Period(begin, end, PeriodType.days());
        int days = p.getDays();
        System.out.println("间隔天数：" + days);

        //计算特定日期是否在该区间内
        Interval interval = new Interval(begin, end);
        boolean contained = interval.contains(new DateTime("2015-03-01"));
        System.out.println("是否是区间内：" + contained);
    }

    /**
     * 日期比较
     */
    @Test
    public void compareTest() {
        DateTime d1 = new DateTime("2015-10-01");
        DateTime d2 = new DateTime("2016-02-01");

        //和系统时间比
        System.out.println(d1.isAfterNow());
        System.out.println(d1.isBeforeNow());
        System.out.println(d1.isEqualNow());

        //和其他日期比
        System.out.println(d1.isAfter(d2));
        System.out.println(d1.isBefore(d2));
        System.out.println(d1.isEqual(d2));
    }

    /**
     * 构建不同时区的DateTime对象
     * 默认使用UTC格式
     * 要格式化一个 Joda 对象，调用它的 toString() 方法，并且如果您愿意的话，
     * 传递一个标准的 ISO-8601 或一个 JDK 兼容的控制字符串，以告诉 JDK 如何执行格式化。
     * 不需要创建单独的 SimpleDateFormat 对象（但是 Joda 的确为那些喜欢自找麻烦的人提供了一个 DateTimeFormatter 类）
     */
    @Test
    public void TimeZoneTest() {
        //默认设置为日本时间
        DateTimeZone.setDefault(DateTimeZone.forID("Asia/Tokyo"));
        DateTime dt1 = new DateTime();
        System.out.println(dt1.toString("yyyy-MM-dd HH:mm:ss"));

        //伦敦时间
        DateTime dt2 = new DateTime(DateTimeZone.forID("Europe/London"));
        System.out.println(dt2.toString("yyyy-MM-dd HH:mm:ss"));

        // 格式化输出
        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);
        System.out.println(dateTime.toString(ISODateTimeFormat.basicDateTime())); // 20000101T000000.000+0900
        System.out.println(dateTime.toString(ISODateTimeFormat.basicDateTimeNoMillis())); // 20000101T000000+0900
        System.out.println(dateTime.toString(ISODateTimeFormat.basicOrdinalDateTime())); // 2000001T000000.000+0900
        System.out.println(dateTime.toString(ISODateTimeFormat.basicWeekDateTime())); // 1999W526T000000.000+0900

        System.out.println(dateTime.toString("MM/dd/yyyy hh:mm:ss.SSSa")); // 01/01/2000 12:00:00.000上午
        System.out.println(dateTime.toString("dd-MM-yyyy HH:mm:ss")); // 01-01-2000 00:00:00
        System.out.println(dateTime.toString("EEEE dd MMMM, yyyy HH:mm:ssa")); // 星期六 01 一月, 2000 00:00:00上午
        System.out.println(dateTime.toString("MM/dd/yyyy HH:mm ZZZZ")); // 01/01/2000 00:00 Asia/Tokyo
        System.out.println(dateTime.toString("MM/dd/yyyy HH:mm Z")); // 01/01/2000 00:00 +0900
    }

    /**
     * 某个特定对象的出生日期 可能为 1999 年 4 月 16 日，但是从技术角度来看，
     * 在保存所有业务值的同时不会了解有关此日期的任何其他信息（比如这是一周中的星期几，或者这个人出生地所在的时区）。
     * 在这种情况下，应当使用 LocalDate
     */
    @Test
    public void localDateTest() {
        LocalDate localDate = new LocalDate(2009, 9, 6);// September 6, 2009
        LocalTime localTime = new LocalTime(13, 30, 26, 0);// 1:30:26PM
    }

    /**
     * DateTime对象转换为JDK时间
     */
    @Test
    public void DateTimeToDate() {
        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);

        // 转换为时间戳 使用Date()的构建函数
        Date d1 = new Date(dateTime.getMillis());
        System.out.println(d1);

        // 直接调用toDate()方法
        Date d2 = dateTime.toDate();
        System.out.println(d2);
    }

    /**
     * 到新年还有多少天
     * 两个时间参数不区分先后
     *
     * @return
     */
    @Test
    public void daysToNewYear() {
        LocalDate fromDate = new LocalDate("2017-12-29");
        LocalDate newYear = fromDate.plusYears(2).withDayOfYear(1);
        Days days = Days.daysBetween(fromDate, newYear);
        System.out.println("----days: " + days + "----" + days.getDays() + "----" + days.getValue(0));
        Days days2 = Days.daysBetween(newYear, fromDate);
        System.out.println("----days: " + days2 + "----" + days2.getDays() + "----" + days2.getValue(0));
    }

}
