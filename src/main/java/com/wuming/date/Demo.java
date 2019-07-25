package com.wuming.date;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Scanner;

/**
 * @author wuming
 * Created on 2017/11/17 21:10
 */
public class Demo {

    /**
     * 输入一个日期(生日，格式：yyyy-MM-dd Or yyyy-MM-dd HH:mm:ss)
     * 计算出今天是你人生的第多少天/小时/分/秒
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("*********请输入生日(yyyy-MM-dd Or yyyy-MM-dd HH:mm:ss)*********");
                String line = scanner.nextLine();
                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }
                DateTimeFormatter format = null;
                if (line.length() == 10) {
                    format = DateTimeFormat.forPattern("yyyy-MM-dd");
                } else {
                    format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                }
                DateTime startDateTime = null;
                try {
                    startDateTime = DateTime.parse(line, format);
                } catch (Exception e) {
                    System.err.println("输入格式错误,请重新输入生日!");
                    continue;
                }
                calDays(startDateTime, new DateTime());
            }
        } finally {
            scanner.close();
        }
    }

    private static void calDays(DateTime startDateTime, DateTime endDateTime) {
        Days days = Days.daysBetween(startDateTime, endDateTime);
        System.out.println("今天是你人生的第" + days.getDays() + "天");

        Hours hours = Hours.hoursBetween(startDateTime, endDateTime);
        System.out.println("现在是你人生的第" + hours.getHours() + "小时");

        Minutes minutes = Minutes.minutesBetween(startDateTime, endDateTime);
        System.out.println("现在是你人生的第" + minutes.getMinutes() + "分钟");

        Seconds seconds = Seconds.secondsBetween(startDateTime, endDateTime);
        System.out.println("当前是你人生的第" + seconds.getSeconds() + "秒");
    }

}
