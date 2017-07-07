package com.wuming.thread;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuming on 2017/6/29.
 */
public class ThreadTest {

    /**
     * 由于 HashMap 非线程安全，多个线程同时像map中put时，
     * 假如key相同，会造成map被hold住的情况(具体原因是map的put操作可能会resize map，相同key同时resize时就会出问题了)
     * 我运行了多次并没有复现这种情况
     *
     * @param args
     */
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

    @Test
    public void test() {
        Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
    }


}
