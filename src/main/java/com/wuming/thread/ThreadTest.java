package com.wuming.thread;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuming on 2017/6/29.
 */
public class ThreadTest {

    @Test
    public void test(){
        Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        final HashMap map = new HashMap();
        final Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 5000000; i++) {
                    map.put(new Integer(i), i);
                }
                System.out.println("t1 over");
            }
        };

        final Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 5000000; i++) {
                    map.put(new Integer(i), i);
                }
                System.out.println("t2 over");
            }
        };
        t1.start();
        t2.start();
    }


}
