package com.wuming.jodaTime;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Date;


/**
 * @author wuming
 * Created on 2017/11/17 20:33
 */
public class JodaTimeTest {

    /**
     * DateTime转换为时间
     */
    @Test
    public void DateTimeToDate() {
        DateTime dt = new DateTime();
        //转换成java.util.Date对象
        Date d1 = new Date(dt.getMillis());
        System.out.println(d1);
        Date d2 = dt.toDate();
        System.out.println(d2);
    }

    /**
     * 构建DateTime对象
     */
    @Test
    public void test() {
        // 直接构建时间对象(默认为UTC时间)
        DateTime dateTime = new DateTime(2015, 12, 21, 0, 0, 0, 333);// 年,月,日,时,分,秒,毫秒
        // 通过时间戳构建
        DateTime dateTime1 = new DateTime(new Date().getTime());
        // 通过字符串构建
        DateTime dateTime2 = new DateTime("2015-12-21"); // 不能使用 2015-12-21 23:22:45 格式，报错
        System.out.println(dateTime2);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime3 = DateTime.parse("2015-12-21 23:22:45", format);
        System.out.println(dateTime3);

        // 格式化输出
        System.out.println(dateTime.toString("yyyy/MM/dd HH:mm:ss EE"));
        // 将字符串解析为DateTime对象
        System.out.println(dateTime.toString("yyyy/MM/dd HH:mm:ss EE"));
    }

    /**
     * 在某个日期上加上90天并输出结果
     */
    @Test
    public void plusTimeTest() {
        DateTime dateTime = new DateTime(2016, 1, 1, 0, 0, 0, 0);
        System.out.println(dateTime.plusDays(90).toString("E MM/dd/yyyy HH:mm:ss.SSS"));
    }

    /**
     * 构建不同时区的DateTime对象
     * 默认使用UTC格式
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
    }

    /**
     * 计算间隔和区间
     */
    @Test
    public void test2() {
        DateTime begin = new DateTime("2015-02-01");
        DateTime end = new DateTime("2016-05-01");

        //计算区间毫秒数
        Duration d = new Duration(begin, end);
        long millis = d.getMillis();
        System.out.println("时间间隔：" + millis);

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

}
