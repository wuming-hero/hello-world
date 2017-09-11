package com.wuming.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 用户分隔符
 * 配合guava的Splitter的使用
 */
public class Splitters {
    public static final Splitter DOT = Splitter.on(".").omitEmptyStrings().trimResults();
    public static final Splitter COMMA = Splitter.on(",").omitEmptyStrings().trimResults();
    public static final Splitter COLON = Splitter.on(":").omitEmptyStrings().trimResults();
    public static final Splitter AT = Splitter.on("@").omitEmptyStrings().trimResults();
    public static final Splitter SLASH = Splitter.on("/").omitEmptyStrings().trimResults();
    public static final Splitter SPACE = Splitter.on(" ").omitEmptyStrings().trimResults();
    public static final Splitter UNDERSCORE = Splitter.on("_").omitEmptyStrings().trimResults();

    public static List<Long> splitToLong(CharSequence sequence, Splitter splitter) {
        List<String> ss = splitter.splitToList(sequence);
        List<Long> res = Lists.newArrayListWithCapacity(ss.size());
        for (String s : ss) {
            res.add(Long.valueOf(Long.parseLong(s)));
        }
        return res;
    }

    public static List<Integer> splitToInteger(CharSequence sequence, Splitter splitter) {
        List<String> ss = splitter.splitToList(sequence);
        List<Integer> res = Lists.newArrayListWithCapacity(ss.size());
        for (String s : ss) {
            res.add(Integer.valueOf(Integer.parseInt(s)));
        }
        return res;
    }
}