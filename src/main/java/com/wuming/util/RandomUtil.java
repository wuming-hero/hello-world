package com.wuming.util;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomUtil {

    /**
     * 生成指定长度的随机数
     *
     * @param size
     * @return
     */
    public static String random(int size) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int j = 0; j < size; j++) {
            sb.append(random.nextInt(9));
        }
        return sb.toString();
    }

    /**
     * 获取一定长度的随机字符串
     *
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String randomString(int length) {
        String base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 返回两个数之间的随机数
     * 前后闭区间[min, max]
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomNumber(int min, int max) {
        Random random = new Random();
        // x % (max - min + 1) 得到的数字为[0, max - min]之间
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 返回两个数之间的随机数
     * 前开后闭区间[min, max)
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomNumber2(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min) + min;
    }

    /**
     * 返回两个数之间的随机数
     * 前开后闭区间[min, max)
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomNumber3(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    /**
     * 返回两个数之间的随机数
     * 前开后闭区间[min, max)
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomNumber4(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
