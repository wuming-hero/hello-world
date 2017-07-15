package com.wuming.thread;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wuming on 2017/7/1.
 * <p>
 * HashMap线程不安全：
 * HashMap在并发执行put操作时会引起死循环，导致CPU利用率接近100%。
 * 因为多线程会导致HashMap的Node链表形成环形数据结构，一旦形成环形数据结构，Node的next节点永远不为空，就会在获取Node时产生死循环。
 */
public class MapTest {

    public final static int THREAD_POOL_SIZE = 5;
    public static Map<String, Integer> hashTable = null;
    public static Map<String, Integer> synchronizedMap = null;
    public static Map<String, Integer> concurrentHashMap = null;

    /**
     * ConcurrentHashMap性能是明显优于 Hashtable 和 SynchronizedMap 的, ConcurrentHashMap 花费的时间比前两个的一半还少
     *
     * @param args
     * @throws InterruptedException
     */
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
                executorService.execute(() -> {
                    for (int i1 = 0; i1 < 500000; i1++) {
                        Integer randomNumber = (int) Math.ceil(Math.random() * 550000);
                        // Retrieve value. We are not using it anywhere
                        Integer value = threadMap.get(String.valueOf(randomNumber));
                        // Put value
                        threadMap.put(String.valueOf(randomNumber), randomNumber);
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


    /********************demo start*************************/
    /**
     * ConcurrentHashMap是并发效率更高的Map，用来替换其他线程安全的Map容器，比如Hashtable和Collections.synchronizedMap。
     * <p>
     * 实际上，并发执行时，线程安全的容器只能保证自身的数据不被破坏，但无法保证业务的行为是否正确。
     * <p>
     * 错误的理解这里的线程安全，不恰当的使用ConcurrentHashMap，往往会导致出现问题。
     * <p>
     * demo1是两个线程操作ConcurrentHashMap，意图将value变为10。
     * 但是，因为多个线程用相同的key调用时，很可能会覆盖相互的结果，造成记录的次数比实际出现的次数少
     */
    @Test
    public void demo1() {
        final Map<String, Integer> count = new ConcurrentHashMap<>();
        final CountDownLatch endLatch = new CountDownLatch(2);
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                Integer value = count.get("a");
                if (null == value) {
                    count.put("a", 1);
                } else {
                    count.put("a", value + 1);
                }
            }
            endLatch.countDown();
        };
        new Thread(task).start();
        new Thread(task).start();

        try {
            endLatch.await();
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用ConcurrentMap定义的方法对demo1进行改进
     * <p>
     * V putIfAbsent(K key, V value)
     * 如果key对应的value不存在，则put进去，返回null。否则不put，返回已存在的value。
     * <p>
     * boolean remove(Object key, Object value)
     * 如果key对应的值是value，则移除K-V，返回true。否则不移除，返回false。
     * <p>
     * boolean replace(K key, V oldValue, V newValue)
     * 如果key对应的当前值是oldValue，则替换为newValue，返回true。否则不替换，返回false。
     */
    @Test
    public void demo2() {
        final Map<String, Integer> count = new ConcurrentHashMap<>();
        final CountDownLatch endLatch = new CountDownLatch(2);
        Runnable task = () -> {
            Integer oldValue, newValue;
            for (int i = 0; i < 1000; i++) {
                while (true) {
                    oldValue = count.get("a");
                    if (null == oldValue) {
                        newValue = 1;
                        if (count.putIfAbsent("a", newValue) == null) {
                            break;
                        }
                    } else {
                        newValue = oldValue + 1;
                        if (count.replace("a", oldValue, newValue)) {
                            break;
                        }
                    }
                }
            }
            endLatch.countDown();
        };
        new Thread(task).start();
        new Thread(task).start();

        try {
            endLatch.await();
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 由于ConcurrentMap中不能保存value为null的值，所以需要处理不存在和已存在两种情况，不过可以使用AtomicInteger来替代
     * <p>
     * 使用AtomicInteger 原子类对demo2进行优化
     */
    @Test
    public void demo3() {
        final Map<String, AtomicInteger> count = new ConcurrentHashMap<>();
        final CountDownLatch endLatch = new CountDownLatch(2);
        Runnable task = () -> {
            AtomicInteger oldValue;
            for (int i = 0; i < 5; i++) {
                oldValue = count.get("a");
                if (null == oldValue) {
                    AtomicInteger zeroValue = new AtomicInteger(0);
                    oldValue = count.putIfAbsent("a", zeroValue);
                    if (null == oldValue) {
                        oldValue = zeroValue;
                    }
                }
                oldValue.incrementAndGet();
            }
            endLatch.countDown();
        };
        new Thread(task).start();
        new Thread(task).start();

        try {
            endLatch.await();
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ConcurrentHashMap 虽然说是线程安全的，
     * 但只能维护自身线程的数据安全，多个线程共同访问和修改同一个key时，就会出现不安全的问题
     * 如下示例就会出问题，运行结果map中的数据并不是想要的
     *
     * @throws InterruptedException
     */
    @Test
    public void concurrentHashMapUnsafe() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                if (concurrentMap.containsKey("a")) {
                    concurrentMap.put("a", concurrentMap.get("a") + 1);
                } else {
                    concurrentMap.put("a", 1);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println(concurrentMap);
    }

    /**
     * 使用ConcurrentHashMap 和 AtomicInter 实现map多线程安全问题
     *
     * @throws InterruptedException
     */
    @Test
    public void safeUseConcurrentHashMap() throws InterruptedException {
        int i = 0;
        while (i++ < 10) {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            Map<String, AtomicInteger> concurrentMap = new ConcurrentHashMap<>();
            for (int j = 0; j < 10000; j++) {
                executorService.execute(() -> {
                    AtomicInteger oldValue = concurrentMap.get("a");
                    if (oldValue == null) {
                        AtomicInteger newValue = new AtomicInteger(0);
                        oldValue = concurrentMap.putIfAbsent("a", newValue);
                        if (oldValue == null) {
                            oldValue = newValue;
                        }
                    }
                    oldValue.addAndGet(1);
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);

            System.out.println(concurrentMap);
        }

    }


    /****************** demo end********************/
}
