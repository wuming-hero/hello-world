package com.wuming.thread;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuming on 2017/7/13.
 * 1、public V get(Object key)不涉及到锁，也就是说获得对象时没有使用锁；
 * <p>
 * 2、put、remove方法要使用锁，但并不一定有锁争用，原因在于ConcurrentHashMap将缓存的变量分到多个Segment，
 * 每个Segment上有一个锁，只要多个线程访问的不是一个Segment就没有锁争用，就没有堵塞，各线程用各自的锁，
 * ConcurrentHashMap缺省情况下生成16个Segment，也就是允许16个线程并发的更新而尽量没有锁争用；
 * <p>
 * 3、Iterator对象的使用，不一定是和其它更新线程同步，获得的对象可能是更新前的对象，
 * ConcurrentHashMap允许一边更新、一边遍历，也就是说在Iterator对象遍历的时候，
 * ConcurrentHashMap也可以进行remove,put操作，且遍历的数据会随着remove,put操作产出变化，
 * 所以希望遍历到当前全部数据的话，要么以ConcurrentHashMap变量为锁进行同步(synchronized该变量)，
 * 要么使用CopiedIterator包装iterator，使其拷贝当前集合的全部数据，但是这样生成的iterator不可以进行remove操作。
 * <p>
 * 一个线程对ConcurrentHashMap增加数据，另外一个线程在遍历时就能获得
 */
public class ConcurrentHashMapTest {

    static Map<Long, String> conMap = new ConcurrentHashMap<>();

    /**
     * ConcurrentHashMap 支持边修改边遍历
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        for (long i = 0; i < 5; i++) {
            conMap.put(i, i + "");
        }

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conMap.put(100l, "100");
            System.out.println("ADD:" + 100);
        });

        Thread thread2 = new Thread(() -> {
            for (Iterator<Map.Entry<Long, String>> iterator = conMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Long, String> entry = iterator.next();
                System.out.println(entry.getKey() + " - " + entry.getValue());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread2.start();

        Thread.sleep(3000);
        System.out.println("--------");

        for (Map.Entry<Long, String> entry : conMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

    }

    /**
     * 边遍历边做删除操作，HashMap 或ArrayList 会报java.util.ConcurrentModificationException异常
     * CConcurrentHashMap 不使用iterator，直接遍历删除则不会报错
     */
    @Test
    public void hashMapTest() {
        Map<Long, String> mReqPacket = new HashMap<>();
        for (long i = 0; i < 15; i++) {
            mReqPacket.put(i, i + "");
        }

        // 边遍历边删除操作，会报java.util.ConcurrentModificationException异常
        for (Map.Entry<Long, String> entry : mReqPacket.entrySet()) {
            long key = entry.getKey();
            String value = entry.getValue();
            if (key < 10) {
                mReqPacket.remove(key);
            }
        }

        // 使用 Iterator 迭代器则可正常操作
        for (Iterator<Map.Entry<Long, String>> iterator = mReqPacket.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Long, String> entry = iterator.next();
            long key = entry.getKey();
            if (key < 10) {
                iterator.remove();
            }
        }

        for (Map.Entry<Long, String> entry : mReqPacket.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * 对ConcurrentHashMap边遍历边删除或者增加操作不会产生异常(可以不用迭代方式删除元素)，
     * 因为其内部已经做了维护，遍历的时候都能获得最新的值。即便是多个线程一起删除、添加元素也没问题
     */
    @Test
    public void concurrentHashMapTest() {
        Map<Long, String> conMap = new ConcurrentHashMap<>();
        for (long i = 0; i < 15; i++) {
            conMap.put(i, i + "");
        }

        for (Map.Entry<Long, String> entry : conMap.entrySet()) {
            long key = entry.getKey();
            if (key < 10) {
                conMap.remove(key);
            }
        }

        for (Map.Entry<Long, String> entry : conMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

}
