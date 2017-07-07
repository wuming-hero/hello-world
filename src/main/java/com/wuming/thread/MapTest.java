package com.wuming.thread;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuming on 2017/7/1.
 */
public class MapTest {

    public final static int THREAD_POOL_SIZE = 5;
    public static Map<String, Integer> hashTable = null;
    public static Map<String, Integer> synchronizedMap = null;
    public static Map<String, Integer> concurrentHashMap = null;

    public static void main(String[] args) throws InterruptedException {
        // Test with Hashtable Object
        hashTable = new Hashtable<>();
        performTest(hashTable);

        // Test with synchronizedMap Object
        synchronizedMap = Collections.synchronizedMap(new HashMap<String, Integer>());
        performTest(synchronizedMap);

        // Test with ConcurrentHashMap Object
        concurrentHashMap = new ConcurrentHashMap<>();
        performTest(concurrentHashMap);
    }

    public static void performTest(final Map<String, Integer> threadMap) throws InterruptedException {
        System.out.println("Test started for: " + threadMap.getClass());
        long averageTime = 0;
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            for (int j = 0; j < THREAD_POOL_SIZE; j++) {
                executorService.execute(new Runnable() {
                    @SuppressWarnings("unused")
                    @Override
                    public void run() {
                        for (int i = 0; i < 500000; i++) {
                            Integer randomNumber = (int) Math.ceil(Math.random() * 550000);
                            // Retrieve value. We are not using it anywhere
                            Integer value = threadMap.get(String.valueOf(randomNumber));
                            // Put value
                            threadMap.put(String.valueOf(randomNumber), randomNumber);
                        }
                    }
                });
            }

            // Make sure executor stops
            executorService.shutdown();
            // Blocks until all tasks have completed execution after a shutdown request
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            long entTime = System.nanoTime();
            long totalTime = (entTime - startTime) / 1000000L;
            averageTime += totalTime;
            System.out.println("2500K entried added/retrieved in " + totalTime + " ms");
        }
        System.out.println("For " + threadMap.getClass() + " the average time is " + averageTime / 5 + " ms\n");
    }
}
