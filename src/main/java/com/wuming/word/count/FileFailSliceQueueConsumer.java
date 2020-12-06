package com.wuming.word.count;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 失败文件切片队列处理
 * 1. 启用线程异步消费处理之前文件统计失败的碎片队列
 * 1.1 如果获取到碎片，调用统计方法进行统计
 * 1.2 如果没有，则休息10s再尝试获取
 *
 * @author wuming
 * Created on 2020-12-06 12:33
 */
public class FileFailSliceQueueConsumer implements Runnable {
    /**
     * 消费失败切片队列
     */
    private ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue;

    /**
     * 切片统计map数据队列
     */
    private ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue;
    /**
     * 失败的大文件切片队列
     */
    private ConcurrentLinkedQueue<BigFileReader> bigFileFailQueue;

    public FileFailSliceQueueConsumer(ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue, ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue, ConcurrentLinkedQueue<BigFileReader> bigFileFailQueue) {
        this.fileFailSliceQueue = fileFailSliceQueue;
        this.wordCountQueue = wordCountQueue;
        this.bigFileFailQueue = bigFileFailQueue;
    }

    @Override
    public void run() {
        System.out.println("fileFailSliceQueueConsume, fail slice queue size: " + fileFailSliceQueue.size());
        while (true) {
            while (!fileFailSliceQueue.isEmpty()) {
                //从队列取出一个元素 排队的人少一个
                BigFileReader fileSliceData = fileFailSliceQueue.poll();
                // 构建基础流信息
                BigFileReader.Builder builder = new BigFileReader.Builder(fileSliceData.getFilePath(), wordCountQueue, bigFileFailQueue);
                builder.withThreadSize(2).withBufferSize(1024 * 1024);
                BigFileReader bigFileReader = builder.build();
                // 设置要统计的失败的流片段
                bigFileReader.setStartEndPairs(fileSliceData.getStartEndPairs());
                // 直接调用切片统计数据
                bigFileReader.countWordForSlice();
            }
            System.out.println("++++++++fileFailSliceQueueConsume+++++++++++++++");
            // 如果是空的,休息10秒再统计
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
