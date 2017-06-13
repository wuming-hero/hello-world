package com.wuming.base;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuming on 2017/6/8.
 */
public class Lambda {

    @Test
    public void test() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello World!");
            }
        }).start();
        TimeUnit.SECONDS.sleep(1000);
    }

    @Test
    public void test2() throws InterruptedException {
        new Thread(() -> System.out.println("Hello World!")).start();
        TimeUnit.SECONDS.sleep(1000);
    }

    @Test
    public void test3(){
        Map<String, Object> map = Maps.newHashMap();
    }
}
