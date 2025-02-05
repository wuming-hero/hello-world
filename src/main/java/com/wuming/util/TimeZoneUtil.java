package com.wuming.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

/**
 * @author manji
 * Created on 2024/12/14 17:56
 */
public class TimeZoneUtil {

    private static final Logger logger = LoggerFactory.getLogger(TimeZoneUtil.class);

    private static final int MAX_OFFSET_HOUR = 14;
    private static final int MIN_OFFSET_HOUR = -12;
    private static final int MAX_WEST_OFFSET = -100;
    private static final int MIN_WEST_OFFSET = -1200;
    private static final int MAX_EAST_OFFSET = 1200;
    private static final int MIN_EAST_OFFSET = 100;


    private static final String DELIMITER = ":";

    private static final String EMPTY_SYMBOL = "";

    private static final String WEST_SYMBOL = "-";

    private static final String EAST_SYMBOL = "+";

    private static final String GMT = "GMT";

    /**
     * 是否可适配
     *
     * @param timeZone 时区
     * @return 是否可适配
     */
    public static boolean canAdaptation(String timeZone) {

        if (timeZone.contains(DELIMITER)) {
            timeZone = timeZone.replace(DELIMITER, EMPTY_SYMBOL);
        }
        if (timeZone.contains(WEST_SYMBOL)) {
            timeZone = timeZone.replace(WEST_SYMBOL, EMPTY_SYMBOL);
        }
        if (timeZone.contains(EAST_SYMBOL)) {
            timeZone = timeZone.replace(EAST_SYMBOL, EMPTY_SYMBOL);
        }

        if (!NumberUtils.isDigits(timeZone)) {
            return false;
        }

        int temp = Integer.parseInt(timeZone);
        if (temp >= MIN_OFFSET_HOUR && temp <= MAX_OFFSET_HOUR) {
            return true;
        } else if (temp >= MIN_WEST_OFFSET && temp <= MAX_WEST_OFFSET) {
            return true;
        } else {
            return temp >= MIN_EAST_OFFSET && temp <= MAX_EAST_OFFSET;
        }
    }

    /**
     * 适配时区
     *
     * @param timeZone 时区
     * @return 适配后的时区格式 +00:00
     */
    public static String adapt(String timeZone) {
        try {
            if (StringUtils.isBlank(timeZone)) {
                return null;
            }

            if (!canAdaptation(timeZone)) {
                return null;
            }

            timeZone = timeZone.replace(":", "");
            String prefix = timeZone.startsWith(WEST_SYMBOL) ? WEST_SYMBOL : EAST_SYMBOL;
            if (timeZone.startsWith(EAST_SYMBOL) || timeZone.startsWith(WEST_SYMBOL)) {
                timeZone = timeZone.substring(1);
            }

            int length = timeZone.length();
            switch (length) {
                case 1:
                    return prefix + "0" + timeZone + ":00";
                case 2:
                    return prefix + timeZone + ":00";
                case 3:
                    String hour = timeZone.substring(0, 1);
                    return prefix + "0" + hour + ":" + timeZone.substring(1);
                case 4:
                    String hour2 = timeZone.substring(0, 2);
                    return prefix + hour2 + ":" + timeZone.substring(2);
                default:
                    return timeZone;
            }
        } catch (Throwable e) {
            logger.error("adapt@TimeZoneAdaptUtils error, timeZone: {}", timeZone, e);
            return null;
        }

    }

    /**
     * 构建时区
     *
     * @param timeZoneStr
     * @return
     */
    public static TimeZone adaptTimeZone(String timeZoneStr) {
        try {
            if (StringUtils.isBlank(timeZoneStr)) {
                return null;
            }
            // 如果已经是GMT开关的，去掉后处理
            if (timeZoneStr.startsWith(GMT)) {
                timeZoneStr = timeZoneStr.replace(GMT, "");
            }
            // 转换为标准格式时区
            String standardTimeZone = adapt(timeZoneStr);
            if (StringUtils.isBlank(standardTimeZone)) {
                return null;
            }
            return TimeZone.getTimeZone(GMT + standardTimeZone);
        } catch (Throwable e) {
            logger.error("adaptTimeZone@TimeZoneAdaptUtils error, timeZone: {}", timeZoneStr, e);
            return null;
        }
    }

}
