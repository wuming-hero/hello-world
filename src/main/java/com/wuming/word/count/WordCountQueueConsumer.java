package com.wuming.word.count;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 切片统计Map数据合并到主统计map
 * 1. 将每个切片的统计数据合并到全局统计Map中
 *
 * @author wuming
 * Created on 2020-12-06 12:33
 */
public class WordCountQueueConsumer implements Runnable {

    private static final byte[] lock = new byte[0];

    /**
     * 切片统计map数据队列
     */
    private ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue;
    /**
     * 总统计数据Map
     */
    private ConcurrentHashMap<String, AtomicLong> wordCountMap;

    /**
     * @param wordCountQueue
     * @param wordCountMap
     */
    public WordCountQueueConsumer(ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue, ConcurrentHashMap<String, AtomicLong> wordCountMap) {
        this.wordCountQueue = wordCountQueue;
        this.wordCountMap = wordCountMap;
    }

    /**
     * 切片数据合并到文件数据
     *
     * @param fileCountMap  全局单词统计计数器
     * @param sliceCountMap 单个文件切片单词统计计数器
     */
    public static void combineMap(ConcurrentHashMap<String, AtomicLong> fileCountMap, ConcurrentHashMap<String, AtomicLong> sliceCountMap) {
        for (Map.Entry<String, AtomicLong> entry : sliceCountMap.entrySet()) {
            String partKey = entry.getKey();
            AtomicLong partCount = entry.getValue();
            synchronized (lock) {
                if (fileCountMap.containsKey(partKey)) {
                    fileCountMap.get(partKey).addAndGet(partCount.longValue());
                } else {
                    fileCountMap.putIfAbsent(partKey, partCount);
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("merge slice data, slice queue size: " + wordCountQueue.size());
        while (true) {
            while (!wordCountQueue.isEmpty()) {
                //从队列取出一个元素 排队的人少一个
                ConcurrentHashMap<String, AtomicLong> sliceWordCountMap = wordCountQueue.poll();
                combineMap(wordCountMap, sliceWordCountMap);
            }
            System.out.println("------>>>>>wordCountQueueConsumer current dataMap: " + wordCountMap.size());
            //System.out.println("current countMap: " + wordCountMap);
            // 如果是空的,休息一秒再统计
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
