package com.wuming.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    private static Calendar calendar;

    /**
     * *******************时间转换为字符串********************
     */
    public static String dateToStr(Date date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Date 转 String 类型失败: " + e);
            }
        }
        return null;

    }

    public static String dateToString(Date date) {
        return dateToStr(date, "yyyy-MM-dd");
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, "yyyyMMdd");
    }

    public static String dateTimeToString(Date date) {
        return dateToStr(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateTimeToStrings(Date date) {
        return dateToStr(date, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static String dateTimeToStr(Date date) {
        return dateToStr(date, "yyyyMMddHHmmss");
    }

    public static String dateTimeToStrs(Date date) {
        return dateToStr(date, "yyyyMMddHHmmssSSS");
    }

    public static String timeToString(Date date) {
        return dateToStr(date, "HH:mm:ss");
    }

    public static String timeToStr(Date date) {
        return dateToStr(date, "HHmmss");
    }

    public static String yearToStr(Date date) {
        return dateToStr(date, "yyyy");
    }

    public static int yearToInt(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(1);
    }

    public static String monthToStr(Date date) {
        return dateToStr(date, "MM");
    }

    public static int monthToInt(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(2) + 1;
    }

    public static String dayToStr(Date date) {
        return dateToStr(date, "dd");
    }

    public static int dayToInt(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(5);
    }

    public static String yearMonthToString(Date date) {
        return dateToStr(date, "yyyy-MM");
    }

    public static String yearMonthToStr(Date date) {
        return dateToStr(date, "yyyyMM");
    }

    public static String monthDayToString(Date date) {
        return dateToStr(date, "MM-dd");
    }

    public static String monthDayToStr(Date date) {
        return dateToStr(date, "MMdd");
    }

    public static String dayMonthYearToStr(Date date) {
        return dateToStr(date, "dd/MM/yyyy");
    }

    public static String yearMonthDayToStr(Date date) {
        return dateToStr(date, "yyyy/MM/dd");
    }

    /**
     * *******************字符串转换为时间********************
     */
    public static Date strToDate(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("String 转 Date 类型失败: " + e);
            }
        }
        return null;
    }

    public static Date stringToDate(String dateStr) {
        return strToDate(dateStr, "yyyy-MM-dd");
    }

    public static Date dateStrToDate(String dateStr) {
        return strToDate(dateStr, "yyyyMMdd");
    }

    public static Date dateStringToDate(String dateStr) {
        return strToDate(dateStr, "yyyy-MM-dd");
    }

    public static Date dateTimeStringToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date dateTimeStrToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, "yyyyMMddHHmmss");
    }

    public static Date dateTimeStrsToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * *******************时间加减运算*****************
     */

    public static Date addYear(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(1, val);
        return gc.getTime();
    }

    public static Date addMonth(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(2, val);
        return gc.getTime();
    }

    public static Date addDate(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DATE, val);
        return gc.getTime();
    }

    public static Date addHour(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.HOUR, val);
        return gc.getTime();
    }

    public static Date addMinute(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(12, val);
        return gc.getTime();
    }

    public static Date addSecond(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.SECOND, val);
        return gc.getTime();
    }

    public static Date subDate(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DATE, -val);
        return gc.getTime();
    }

    public static Date subHour(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(10, -val);
        return gc.getTime();
    }

    public static Date subMinute(Date date, int val) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(12, -val);
        return gc.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static int getDayOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(6);
    }

    public static int sumDayByYearMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1, year);
        c.set(2, month - 1);
        return c.getActualMaximum(5);
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, c.getActualMaximum(5));
        return c.getTime();
    }

    public static Date getLastDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, c.getActualMaximum(5));
        return c.getTime();
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, c.getActualMinimum(5));
        return c.getTime();
    }

    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, c.getActualMinimum(5));
        return c.getTime();
    }

    public static List<Date> getDateList(Date startDate, Date endDate) {
        Date tempDate = startDate;
        List<Date> dateList = new ArrayList<>();
        if (isSameDay(startDate, endDate)) {
            dateList.add(tempDate);
        } else {
            while (tempDate.before(endDate)) {
                dateList.add(tempDate);
                tempDate = addDays(tempDate, 1);
            }
            dateList.add(tempDate);
        }
        return dateList;
    }

    public static List<String> getDateList(String startDate, String endDate, String format) {
        List<String> sDateList = new ArrayList<>();
        Date periodDate = strToDate(startDate, format);
        if (startDate.equals(endDate)) {
            sDateList.add(dateToStr(periodDate, format));
        } else {
            while (periodDate.before(strToDate(endDate, format))) {
                sDateList.add(dateToStr(periodDate, format));
                periodDate = addDays(periodDate, 1);
            }
            sDateList.add(dateToStr(periodDate, format));
        }
        return sDateList;
    }

    public static List<String> getDateToStrList(String startDate, String endDate, String format) {
        return getDateList(startDate, endDate, format);
    }

    public static List<String> getDateToStrList(String startDate, String endDate) {
        return getDateList(startDate, endDate, "yyyy-MM-dd");
    }

    public static List<Date> getYearMonthAllDate(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        List<Date> dateList = new ArrayList();
        int size = c.getActualMaximum(5);
        for (int i = 0; i < size; i++) {
            c.set(5, i + 1);
            dateList.add(c.getTime());
        }
        return dateList;
    }

    public static int getDays(Date sd, Date ed) {
        return (int) ((ed.getTime() - sd.getTime()) / 86400000L);
    }

    public static Calendar mergeDateTime(Date date, Time time) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        if (time != null) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(time);
            cal.set(11, temp.get(11));
            cal.set(12, temp.get(12));
            cal.set(13, temp.get(13));
            cal.set(14, temp.get(14));
        }
        return cal;
    }

    public static int diff_in_date(Date d1, Date d2) {
        return (int) (d1.getTime() - d2.getTime()) / 86400000;
    }

    public static Calendar getDateBegin(int year, int month, int date) {
        Calendar begin_time = Calendar.getInstance();
        begin_time.set(1, year);
        begin_time.set(2, month - 1);
        begin_time.set(5, date);
        begin_time.set(11, 0);
        begin_time.set(12, 0);
        begin_time.set(13, 0);
        begin_time.set(14, 0);
        return begin_time;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if ((date1 == null) || (date2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(0) == cal2.get(0)) && (cal1.get(1) == cal2.get(1)) && (cal1.get(6) == cal2.get(6));
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        if ((date1 == null) || (date2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return date1.getTime() == date2.getTime();
    }

    public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.getTime().getTime() == cal2.getTime().getTime();
    }

    public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(14) == cal2.get(14)) && (cal1.get(13) == cal2.get(13)) && (cal1.get(12) == cal2.get(12))
                && (cal1.get(10) == cal2.get(10)) && (cal1.get(6) == cal2.get(6)) && (cal1.get(1) == cal2.get(1))
                && (cal1.get(0) == cal2.get(0)) && (cal1.getClass() == cal2.getClass());
    }

    public static boolean isDateBefore(Date start, Date end) {
        GregorianCalendar sgc = new GregorianCalendar();
        GregorianCalendar egc = new GregorianCalendar();
        sgc.setTime(start);
        egc.setTime(end);
        return sgc.before(egc);
    }

    public static boolean isDateBetweenStartAndEnd(Date start, Date end, Date date) {
        if ((isDateBefore(start, end)) && (isDateBefore(start, date)) && (isDateBefore(date, end))) {
            return true;
        }
        return false;
    }

    public static boolean isDateBetween(Date start, Date end, Date date) {
        int sd = strToInt(dayToStr(start));
        int ed = strToInt(dayToStr(end));
        int d = strToInt(dayToStr(date));
        return (sd <= ed) && (sd <= d) && (d <= ed);
    }

    public static boolean isDateLe(Date date1, Date date2) {
        int d1 = strToInt(dayToStr(date1));
        int d2 = strToInt(dayToStr(date2));
        return d1 <= d2;
    }

    public static boolean isDateGe(Date date1, Date date2) {
        int d1 = strToInt(dayToStr(date1));
        int d2 = strToInt(dayToStr(date2));
        return d1 >= d2;
    }

    public static boolean yearMonthBetweenStartAndEnd(Date start, Date end, Date date) {
        int sd = strToInt(yearMonthToStr(start).replace("-", ""));
        int ed = strToInt(yearMonthToStr(end).replace("-", ""));
        int d = strToInt(yearMonthToStr(date).replace("-", ""));
        return (sd <= ed) && (sd <= d) && (d <= ed);
    }

    public static boolean yearMonthBetweenStartAndEnd(Date start, Date end, String yearMonth) {
        int sd = strToInt(yearMonthToStr(start).replace("-", ""));
        int ed = strToInt(yearMonthToStr(end).replace("-", ""));
        int ym = strToInt(yearMonth.replace("-", ""));
        return (sd <= ed) && (sd <= ym) && (ym <= ed);
    }

    public static boolean afterCurDate(Date date) {
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return c.after(now);
    }

    public static String getMonthStr(String str) {
        String[] monthOfYear = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        if ("01".equals(str)) {
            str = monthOfYear[0];
        } else if ("02".equals(str)) {
            str = monthOfYear[1];
        } else if ("03".equals(str)) {
            str = monthOfYear[2];
        } else if ("04".equals(str)) {
            str = monthOfYear[3];
        } else if ("05".equals(str)) {
            str = monthOfYear[4];
        } else if ("06".equals(str)) {
            str = monthOfYear[5];
        } else if ("07".equals(str)) {
            str = monthOfYear[6];
        } else if ("08".equals(str)) {
            str = monthOfYear[7];
        } else if ("09".equals(str)) {
            str = monthOfYear[8];
        } else if ("10".equals(str)) {
            str = monthOfYear[9];
        } else if ("11".equals(str)) {
            str = monthOfYear[10];
        } else if ("12".equals(str)) {
            str = monthOfYear[11];
        } else {
            str = "ERROR";
        }
        return str;
    }

    public static int getNumByStrMonth(String strMonth) {
        if (strMonth.equals("January")) {
            return 1;
        }
        if (strMonth.equals("February")) {
            return 2;
        }
        if (strMonth.equals("March")) {
            return 3;
        }
        if (strMonth.equals("April")) {
            return 4;
        }
        if (strMonth.equals("May")) {
            return 5;
        }
        if (strMonth.equals("June")) {
            return 6;
        }
        if (strMonth.equals("July")) {
            return 7;
        }
        if (strMonth.equals("August")) {
            return 8;
        }
        if (strMonth.equals("September")) {
            return 9;
        }
        if (strMonth.equals("October")) {
            return 10;
        }
        if (strMonth.equals("November")) {
            return 11;
        }
        if (strMonth.equals("December")) {
            return 12;
        }
        return 0;
    }

    public static String IntegerTo(Integer i) {
        String str = "";
        if (i.intValue() < 10) {
            str = "0" + i;
        } else {
            str = String.valueOf(i);
        }
        return str;
    }

    public static String getShortTime(String time) {
        calendar = Calendar.getInstance();
        calendar.setTime(dateTimeStringToDate(time));
        String shortstring = null;
        long now = Calendar.getInstance().getTimeInMillis();
        Date date = dateTimeStringToDate(time);
        if (date == null) {
            return shortstring;
        }
        long deltime = (now - date.getTime()) / 1000L;
        if (deltime > 31536000L) {
            shortstring = time;
        } else if (deltime > 86400L) {
            if ((int) (deltime / 86400L) <= 1) {
                shortstring = "昨天 " + IntegerTo(Integer.valueOf(calendar.get(11))) + ":"
                        + IntegerTo(Integer.valueOf(calendar.get(12)));
            } else if ((int) (deltime / 86400L) <= 2) {
                shortstring = "前天 " + IntegerTo(Integer.valueOf(calendar.get(11))) + ":"
                        + IntegerTo(Integer.valueOf(calendar.get(12)));
            } else if ((int) (deltime / 86400L) <= 186) {
                shortstring = IntegerTo(Integer.valueOf(calendar.get(2) + 1)) + "月"
                        + IntegerTo(Integer.valueOf(calendar.get(5))) + "日 "
                        + IntegerTo(Integer.valueOf(calendar.get(11))) + ":"
                        + IntegerTo(Integer.valueOf(calendar.get(12)));
            } else {
                shortstring = time;
            }
        } else if (deltime > 3600L) {
            if ((int) (deltime / 3600L) < 12) {
                shortstring = (int) (deltime / 3600L) + "小时前";
            } else if ((int) (deltime / 3600L) < 24) {
                shortstring = "昨天 " + IntegerTo(Integer.valueOf(calendar.get(11))) + ":"
                        + IntegerTo(Integer.valueOf(calendar.get(12)));
            } else {
                shortstring = "今天 " + IntegerTo(Integer.valueOf(calendar.get(11))) + ":"
                        + IntegerTo(Integer.valueOf(calendar.get(12)));
            }
        } else if (deltime > 60L) {
            shortstring = IntegerTo(Integer.valueOf((int) (deltime / 60L))) + "分前";
        } else if (deltime > 1L) {
            shortstring = "刚刚";
        } else {
            shortstring = "刚刚";
        }
        return shortstring;
    }

    public static String getTime(Date date) {
        String todySDF = "今天 HH:mm";
        String yesterDaySDF = "昨天 HH:mm";
        String otherSDF = "M月d日 HH:mm";
        SimpleDateFormat sfd = null;
        String time = "";
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(now);
        targetCalendar.set(11, 0);
        targetCalendar.set(12, 0);
        if (dateCalendar.after(targetCalendar)) {
            sfd = new SimpleDateFormat(todySDF);
            time = sfd.format(date);
            return time;
        }
        targetCalendar.add(5, -1);
        if (dateCalendar.after(targetCalendar)) {
            sfd = new SimpleDateFormat(yesterDaySDF);
            time = sfd.format(date);
            return time;
        }
        sfd = new SimpleDateFormat(otherSDF);
        time = sfd.format(date);
        return time;
    }

    public static Integer DateToTimestamp(Date time) {
        Timestamp ts = new Timestamp(time.getTime());
        return Integer.valueOf((int) (ts.getTime() / 1000L));
    }

    public static Date timeStampToDate(String seconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(new Long(seconds));
        return dateTimeStringToDate(d);
    }

    /**
     * 今天还剩余多少秒
     *
     * @return 今天剩余秒数
     */
    public static long getTodaySecondRemaining() {
        Calendar curDate = Calendar.getInstance();
        Calendar tomorrowDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DATE) + 1, 0, 0, 0);
        return (tomorrowDate.getTimeInMillis() - curDate.getTimeInMillis()) / 1000;
    }

    /**
     * @return 获得当天0点时间
     */
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();

    }

    /**
     * @return 获得当天24点时间
     */
    public static Date getTimesNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    // 获取某天零点
    public static Date getStartTimeOfDay(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }

    /**
     * 根据时间类型比较时间大小 String nowTime = new SimpleDateFormat("HH:MM").format(new
     * Date()); int i = DateCompare(nowTime,"06:00","HH:MM");
     *
     * @param source
     * @param target
     * @param type   "YYYY-MM-DD" "yyyyMMdd HH:mm:ss" 类型可自定义
     * @return 0 ：source和traget时间相同 1 ：source比traget时间大 -1：source比traget时间小
     * @throws Exception
     */
    public static int DateCompare(String source, String target, String type) throws Exception {
        int ret = 2;
        SimpleDateFormat format = new SimpleDateFormat(type);
        Date sourcedate = format.parse(source);
        Date targetdate = format.parse(target);
        ret = sourcedate.compareTo(targetdate);
        return ret;
    }

    public static boolean betweenTime(Date date, String beginTime, String endTime) {
        return betweenTime(date, beginTime, endTime, null);
    }

    public static boolean betweenTime(Date date, String beginTime, String endTime, String format) {
        if (StringUtils.isBlank(format)) {
            format = "HH:mm:ss";
        }
        String nowTime = new SimpleDateFormat(format).format(date);
        try {
            int i1 = DateCompare(nowTime, beginTime, format);
            int i2 = DateCompare(nowTime, endTime, format);
            if (i1 >= 0 && i2 <= 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("{}", e);
            return false;
        }
    }

    private static int strToInt(String str) {
        return strToInt(str, 0);
    }

    private static int strToInt(String str, int def) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("String 转 int 类型失败: " + e);
            }
        }
        return def;
    }

    public static boolean isLeapYear(int year) {
        GregorianCalendar gc = new GregorianCalendar();
        return gc.isLeapYear(year);
    }

    public static boolean isLeapYear(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        return gc.isLeapYear(yearToInt(date));
    }

    public static Date nextLeapDay(Date since) {
        since = beginOfDay(since);
        Calendar cal = Calendar.getInstance();
        cal.setTime(since);
        int year = cal.get(Calendar.YEAR);
        if (isLeapYear(year)) {
            Date leapDay = dateStrToDate(year + "0229");
            if (!since.after(leapDay))
                return leapDay;
        }
        while (!isLeapYear(++year))
            ;
        return dateStrToDate(year + "0229");
    }

    public static Date beginOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }

    public static Date beginOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date endOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, -1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static String humanRead(Date date) {
        Date now = new Date();
        long delta = now.getTime() - date.getTime();
        boolean before = (delta >= 0);
        delta = delta < 0 ? -delta : delta;
        delta /= 1000;
        String s;
        if (delta <= 60) {
            return "1分钟内";
        } else if (delta < 3600) {
            delta /= 60;
            if (delta == 30)
                s = "半个小时";
            else
                s = delta + "分钟";
        } else if (delta < 86400) {
            double d = delta / 3600d;
            long h = (long) d;
            long m = (long) ((d - h) * 3600);
            m /= 60;
            if (m == 0)
                s = h + "个小时";
            else if (m == 30)
                s = h + "个半小时";
            else
                s = h + "个小时" + m + "分钟";
        } else if (delta < 2592000) {
            s = delta / 86400 + "天";
        } else if (delta < 31104000) {
            s = delta / 2592000 + "个月";
        } else {
            s = delta / 31104000 + "年";
        }
        return s + (before ? "前" : "后");
    }

    /**
     * @author soddabao
     * @url http://www.blogjava.net/soddabao/archive/2007/01/04/91729.html
     */
    public static class Lunar {
        final static String chineseNumber[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
        final static long[] lunarInfo = new long[]{0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554,
                0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,
                0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2,
                0x04970, 0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
                0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0,
                0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50,
                0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
                0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0,
                0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
                0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0,
                0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0,
                0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260,
                0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520,
                0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};
        static FastDateFormat chineseDateFormat = FastDateFormat.getInstance("yyyy年MM月dd日");
        private int year;
        private int month;
        private int day;
        private boolean leap;

        /**
         * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
         * dayCyl5:与1900年1月31日相差的天数,再加40 ?
         *
         * @param cal
         * @return
         */
        public Lunar(Calendar cal) {
            @SuppressWarnings("unused")
            int yearCyl, monCyl, dayCyl;
            int leapMonth = 0;
            Calendar baseCalendar = Calendar.getInstance();
            baseCalendar.set(Calendar.YEAR, 1900);
            baseCalendar.set(Calendar.MONTH, 0);
            baseCalendar.set(Calendar.DAY_OF_MONTH, 31);
            baseCalendar.set(Calendar.HOUR, 0);
            baseCalendar.set(Calendar.MINUTE, 0);
            baseCalendar.set(Calendar.SECOND, 0);
            // 求出和1900年1月31日相差的天数
            int offset = (int) ((cal.getTime().getTime() - baseCalendar.getTimeInMillis()) / 86400000L);
            dayCyl = offset + 40;
            monCyl = 14;

            // 用offset减去每农历年的天数
            // 计算当天是农历第几天
            // i最终结果是农历的年份
            // offset是当年的第几天
            int iYear, daysOfYear = 0;
            for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
                daysOfYear = yearDays(iYear);
                offset -= daysOfYear;
                monCyl += 12;
            }
            if (offset < 0) {
                offset += daysOfYear;
                iYear--;
                monCyl -= 12;
            }
            // 农历年份
            year = iYear;

            yearCyl = iYear - 1864;
            leapMonth = leapMonth(iYear); // 闰哪个月,1-12
            leap = false;

            // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
            int iMonth, daysOfMonth = 0;
            for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
                // 闰月
                if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                    --iMonth;
                    leap = true;
                    daysOfMonth = leapDays(year);
                } else
                    daysOfMonth = monthDays(year, iMonth);

                offset -= daysOfMonth;
                // 解除闰月
                if (leap && iMonth == (leapMonth + 1))
                    leap = false;
                if (!leap)
                    monCyl++;
            }
            // offset为0时，并且刚才计算的月份是闰月，要校正
            if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
                if (leap) {
                    leap = false;
                } else {
                    leap = true;
                    --iMonth;
                    --monCyl;
                }
            }
            // offset小于0时，也要校正
            if (offset < 0) {
                offset += daysOfMonth;
                --iMonth;
                --monCyl;
            }
            month = iMonth;
            day = offset + 1;
        }

        // ====== 传回农历 y年的总天数
        final private static int yearDays(int y) {
            int i, sum = 348;
            for (i = 0x8000; i > 0x8; i >>= 1) {
                if ((lunarInfo[y - 1900] & i) != 0)
                    sum += 1;
            }
            return (sum + leapDays(y));
        }

        // ====== 传回农历 y年闰月的天数
        final private static int leapDays(int y) {
            if (leapMonth(y) != 0) {
                if ((lunarInfo[y - 1900] & 0x10000) != 0)
                    return 30;
                else
                    return 29;
            } else
                return 0;
        }

        // ====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
        final private static int leapMonth(int y) {
            return (int) (lunarInfo[y - 1900] & 0xf);
        }

        // ====== 传回农历 y年m月的总天数
        final private static int monthDays(int y, int m) {
            if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
                return 29;
            else
                return 30;
        }

        // ====== 传入 月日的offset 传回干支, 0=甲子
        final private static String cyclicalm(int num) {
            final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
            final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
            return (Gan[num % 10] + Zhi[num % 12]);
        }

        public static String getChinaDayString(int day) {
            String chineseTen[] = {"初", "十", "廿", "卅"};
            int n = day % 10 == 0 ? 9 : day % 10 - 1;
            if (day > 30)
                return "";
            if (day == 10)
                return "初十";
            else
                return chineseTen[day / 10] + chineseNumber[n];
        }

        // ====== 传回农历 y年的生肖
        final public String animalsYear() {
            final String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
            return Animals[(year - 4) % 12];
        }

        // ====== 传入 offset 传回干支, 0=甲子
        final public String cyclical() {
            int num = year - 1900 + 36;
            return (cyclicalm(num));
        }

        public Date toDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            return cal.getTime();
        }

        @Override
        public String toString() {
            return year + "年" + (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
        }

    }
}
