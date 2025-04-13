package com.wuming.util;

/**
 * 62进制编码转换
 *
 * @author manji
 * Created on 2025/4/13 07:46
 */
public class Base62 {
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int RADIX = 62;

    /**
     * 将数值转换为Base62字符串
     *
     */
    public static String encode(long num) {
        if (num == 0) return String.valueOf(CHARSET.charAt(0));
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % RADIX);
            sb.append(CHARSET.charAt(remainder));
            num = num / RADIX;
        }
        // 反转以获取正确顺序
        return sb.reverse().toString();
    }

    // 将Base62字符串转换为数值
    public static long decode(String str) {
        long num = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            num = num * RADIX + CHARSET.indexOf(c);
        }
        return num;
    }
}
