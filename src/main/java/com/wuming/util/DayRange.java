package com.wuming.util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Map;

/**
 * 时间工具，获得时间区间，但入参格式必须是 yyyy-MM-dd
 * 获得当天的开始时间和当天的结束时间
 * [2017-08-13 00:00:00, 2017-08-14 00:00:00)
 */
public class DayRange {

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTime startAt;
    private DateTime endAt;

    public DayRange(String startAtText, String endAtText) {
        if (!Strings.isNullOrEmpty(startAtText)) {
            this.startAt = DTF.parseDateTime(startAtText).withTimeAtStartOfDay();
        }
        if (!Strings.isNullOrEmpty(endAtText)) {
            this.endAt = DTF.parseDateTime(endAtText).withTimeAtStartOfDay().plusDays(1);

            if ((this.startAt != null) && (!this.startAt.isBefore(this.endAt))) {
                this.endAt = this.startAt.plusDays(1);
            }
        }
    }

    public static DayRange of(String startAtText, String endAtText) {
        return new DayRange(startAtText, endAtText);
    }

    public static DayRange from(String startAtText, String endAtText) {
        return new DayRange(startAtText, endAtText);
    }

    public Map<String, Object> toMap(String startAtKey, String endAtKey) {
        Map<String, Object> map = Maps.newHashMap();
        if ((!Strings.isNullOrEmpty(startAtKey)) && (this.startAt != null)) {
            map.put(startAtKey, this.startAt.toDate());
        }
        if ((!Strings.isNullOrEmpty(endAtKey)) && (this.endAt != null)) {
            map.put(endAtKey, this.endAt.toDate());
        }
        return map;
    }

    public Date start() {
        return this.startAt == null ? null : this.startAt.toDate();
    }

    public Date end() {
        return this.endAt == null ? null : this.endAt.toDate();
    }
}