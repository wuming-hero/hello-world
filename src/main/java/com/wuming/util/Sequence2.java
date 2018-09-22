package com.wuming.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自增时间戳序列器
 * <p>
 * 支持同一毫秒最多生成999笔序列号<br>
 *
 * @author wuming
 * Created on 2018/9/21 09:39
 */
public class Sequence2 {

    private static final ReentrantLock lock = new ReentrantLock();
    // 因为有锁，所以是变成了线程安全的，省去每次 new 的消耗，耗时降低约一半
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    // 默认1个大小
    private static HashMap<String, AtomicInteger> cacheMap = new HashMap<>(1);

    public static String getTimeStampSequence() {
        String timestamp = null;
        String inc = null;
        lock.lock();
        try {
            timestamp = sdf.format(new Date());
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
