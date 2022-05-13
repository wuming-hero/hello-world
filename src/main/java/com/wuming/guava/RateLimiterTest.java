package com.wuming.guava;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

/**
 * ReteLimiter使用
 * <p>
 * https://guava.dev/releases/23.0/api/docs/com/google/common/util/concurrent/RateLimiter.html
 *
 * @author manji
 * Created on 2022/5/13 12:41
 */
public class RateLimiterTest {

    /**
     * 固定速率处理请求
     * 场景：某些第三方接口限制请求的qps，否则报错
     */
    @Test
    public void fixedRateTest() {
        // 固定 2个/s
        final RateLimiter rateLimiter = RateLimiter.create(1.0); // rate is "2 permits per second"
        for (int i = 0; i < 10; i++) {
            rateLimiter.acquire(); // may wait
            System.out.println("i" + System.currentTimeMillis());
            System.out.println("-----------");
        }
    }

    /**
     * 限速1个/s
     * rateLimiter.acquire(2) 2 个处理一次，故需要等2秒执行一次
     *
     * @throws InterruptedException
     */
    @Test
    public void waitDataTest() throws InterruptedException {
        final RateLimiter rateLimiter = RateLimiter.create(1.0); // rate = 1 permits per second
        byte[] bytes = "abcdefg".getBytes();
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes.length);
            double acquire = rateLimiter.acquire(2);
            System.out.println(acquire + "----" + new String(new byte[]{bytes[i]}));
//            Thread.sleep(1000);

        }

    }

}
