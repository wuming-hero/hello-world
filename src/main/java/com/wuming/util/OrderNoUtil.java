package com.wuming.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自增时间戳序列器
 * <p>
 * 返回 20 位长度纯数字订单号
 * 支持同一毫秒最多生成999笔序列号
 * 如果服务器时间回调，有可能生成新生成的序列号与之前的重复
 *
 * @author wuming
 * Created on 2018/9/21 09:39
 */
public class OrderNoUtil {

    private static final ReentrantLock lock = new ReentrantLock();
    // 默认1个大小
    private static HashMap<String, AtomicInteger> cacheMap = new HashMap<>(1);

    public static String createOrderNo() {
        String timestamp;
        String inc;
        lock.lock();
        try {
            timestamp = DateTime.now().toString("yyyyMMddHHmmssSSS");
            AtomicInteger value = cacheMap.get(timestamp);
            if (value == null) {
                cacheMap.clear();
                int defaultStartValue = 0;
                cacheMap.put(timestamp, new AtomicInteger(defaultStartValue));
                inc = String.valueOf(defaultStartValue);
            } else {
                inc = String.valueOf(value.addAndGet(1));
            }
        } finally {
            lock.unlock();
        }
        return timestamp + StringUtils.leftPad(inc, 3, '0');
    }

}
