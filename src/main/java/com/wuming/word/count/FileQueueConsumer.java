package com.wuming.word.count;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件队列消费者
 * 1. 异步尝试从全局文件队列中取出文件
 * 1.1 如果读出来文件不为空，交给 cn.dyz.tools.file.BigFileReader 处理
 * 1.2 如果读出来文件为空，休息5秒再尝试读取
 *
 * @author wuming
 * Created on 2020-12-06 12:33
 */
public class FileQueueConsumer implements Runnable {
    /**
     * 无界线程安全的文件路径队列
     */
    private ConcurrentLinkedQueue<String> filePathQueue;
    /**
     * 无界线程安全队列的切片统计map数据队列
     */
    private ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue;
    /**
     * 失败的大文件切片队列
     */
    private ConcurrentLinkedQueue<BigFileReader> bigFileFailQueue;

    public FileQueueConsumer(ConcurrentLinkedQueue<String> filePathQueue, ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue, ConcurrentHashMap<String, AtomicLong> wordCountMap, ConcurrentLinkedQueue<BigFileReader> bigFileFailQueue) {
        this.filePathQueue = filePathQueue;
        this.wordCountQueue = wordCountQueue;
        this.bigFileFailQueue = bigFileFailQueue;
    }

    @Override
    public void run() {
        System.out.println("consume filePathQueue, filePathQueue size: " + filePathQueue.size());
        while (true) {
            while (!filePathQueue.isEmpty()) {
                //从队列取出一个元素 排队的人少一个
                String filePath = filePathQueue.poll();
                BigFileReader.Builder builder = new BigFileReader.Builder(filePath, wordCountQueue, bigFileFailQueue);
                builder.withThreadSize(10).withBufferSize(1024 * 1024);
                BigFileReader bigFileReader = builder.build();
                bigFileReader.countWord();
            }
            System.out.println("---------fileQueueConsumer, 每5秒/次尝试从文件队列读取文件---------");
            // 如果是空的,休息5秒再尝试读取
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
