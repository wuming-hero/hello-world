package com.wuming.util;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * 订单号生成工具类
 * <p>
 * TODO 高并发下不能保证唯一
 * 可以借助Sequence 或 Sequence2工具类实现 高并发唯一订单号
 *
 * @author yangyang
 * @create 16/6/6 19:42
 * @function
 */
@SuppressWarnings("all")
public class OrderIdUtil {

    public static String createOrderId() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        String time = String.valueOf(System.currentTimeMillis());
        String hash = String.valueOf("".hashCode());
        String random = RandomUtil.random(10);
        String md5 = MD5Util.encode(pid + time + hash + random);
        return DateUtil.dateTimeToStr(new Date()) + md5.substring(0, 5).toLowerCase();
    }

    public static String createOrderId(Integer uid) {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        String time = String.valueOf(System.currentTimeMillis());
        String hash = String.valueOf("".hashCode());
        String random = RandomUtil.random(10);
        String md5 = MD5Util.encode(uid + pid + time + hash + random);
        return DateUtil.dateTimeToStr(new Date()) + uid + md5.substring(0, 5).toLowerCase();
    }

}
