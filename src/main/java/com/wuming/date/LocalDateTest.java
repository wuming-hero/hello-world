package com.wuming.date;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * JDK 1.8 日期、时间处理
 *
 * @author wuming
 * Created on 2019-07-20 18:09
 */
public class LocalDateTest {

    /**
     * LocalDateTime获取毫秒数
     */
    @Test
    public void test1() {
        // 获取秒数
        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        System.out.println("second: " + second);
        Long second2 = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        System.out.println("second2: " + second2);
        Long second3 = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        System.out.println("second3: " + second3);

        // 获取毫秒数
        Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long milliSecond2 = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println("millSecond: " + milliSecond);
        System.out.println("millSecond2: " + milliSecond2);

    }

    /**
     * Date to LocalDateTime
     */
    @Test
    public void DateToLocalDateTime() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // 方法一
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        System.out.println("Date:" + date + "---->LocalDateTime = " + localDateTime);

        // 方法二
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date.toInstant(), zoneId);
        System.out.println("Date:" + date + "---->LocalDateTime = " + localDateTime2);
    }

    /**
     * LocalDateTime to Date
     */
    @Test
    public void LocalDateTimeToDate() {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.now();
        // 方法一
        Date date = Date.from(LocalDateTime.now().atZone(zoneId).toInstant());
        System.out.println("LocalDateTime:" + localDateTime + "---->Date = " + date);
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    @Test
    public void dateTimeTest() {
        // 取当前日期：
        LocalDate today = LocalDate.now();
        // 根据年月日取日期：
        LocalDate localDate = LocalDate.of(2014, 12, 25);
        // 根据字符串取：严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式
        LocalDate endOfFeb = LocalDate.parse("2014-02-28");
        // 取本月第1天：
        LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println("first day of current month: " + firstDayOfThisMonth);

        // 取本月第2天：
        LocalDate secondDayOfThisMonth = today.withDayOfMonth(2);
        System.out.println("second day of current month: " + secondDayOfThisMonth);

        // 取本月最后一天，再也不用计算是28，29，30还是31：
        LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("last day of current month: " + lastDayOfThisMonth);

        // 取下一天：
        LocalDate firstDayOfNextMonth = lastDayOfThisMonth.plusDays(1);
        System.out.println("first day of next month: " + firstDayOfNextMonth);

        // 取2017年1月第一个周一，用Calendar要死掉很多脑细胞：
        LocalDate firstMondayOf2019 = LocalDate.parse("2019-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        System.out.println("first monday of 2019: " + firstMondayOf2019);

        System.out.println(LocalDateTime.now().getYear());
        System.out.println(LocalDateTime.now().getNano());

    }

}
