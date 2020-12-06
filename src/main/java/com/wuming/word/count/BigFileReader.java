package com.wuming.word.count;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 大文件解析器
 * <p>
 * 1. 基于线程数对文件进行切片
 * 2. 每个切片使用 BufferedReader 进行读取
 * 3. 基于 ConcurrentHashMap 实时统计每个切片的统计数据
 */
public class BigFileReader {

    /**
     * 静态代码块锁
     */
    private static final byte[] lock = new byte[0];
    // 线程池线程大小
    private int threadSize;
    // 一次读取文件buffer大小
    private int bufferSize;
    // 线程池
    private ExecutorService executorService;
    // 文件路径
    private String filePath;
    // 文件名称
    private String fineName;
    // 文件大小
    private long fileLength;
    // 基于文件的RandomAccessFile 类,用来随机从指定位置读取文件
    private RandomAccessFile randomAccessFile;
    // 文件切片
    private Set<StartEndPair> startEndPairs;
    // 全局数据统计队列
    private ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue;
    // 失败的文件队列
    private ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue;

    /**
     * 大文件基础信息
     *
     * @param filePath      文件路径
     * @param startEndPairs 文件碎片列表
     */
    private BigFileReader(String filePath, Set<StartEndPair> startEndPairs) {
        this.filePath = filePath;
        this.startEndPairs = startEndPairs;
    }

    /**
     * 私有构造器
     *
     * @param file
     * @param bufferSize
     * @param threadSize
     * @param wordCountQueue
     */
    private BigFileReader(File file, int bufferSize, int threadSize, ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue, ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue) {
        this.fileLength = file.length();
        this.fineName = file.getName();
        this.filePath = file.getPath();
        this.bufferSize = bufferSize;
        this.threadSize = threadSize;
        try {
            this.randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.executorService = Executors.newFixedThreadPool(threadSize);
        this.startEndPairs = new HashSet<>();
        this.wordCountQueue = wordCountQueue;
        this.fileFailSliceQueue = fileFailSliceQueue;
    }

    /**
     * 文件读取入口
     */
    public void countWord() {
        long everySize = this.fileLength / this.threadSize;
        // 1.先对文件进行切片
        try {
            calculateStartEnd(0, everySize);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // 2. 使用多线程分片读取
        countWordForSlice();
    }

    /**
     * 计算之前失败的文件切片
     */
    public void countWordForSlice() {
        try {
            CountDownLatch latch = new CountDownLatch(startEndPairs.size());
            Set<StartEndPair> failSliceSet = new HashSet<>(startEndPairs.size());
            // 使用多线程分片读取
            for (StartEndPair pair : startEndPairs) {
                System.out.println("分片读取：" + pair);
                Future future = executorService.submit(new SliceReaderTask(pair, wordCountQueue, latch));
                Boolean sliceFlag = (Boolean) future.get();
                System.out.println("分片读取结果：" + sliceFlag);
                // 如果处理不成功，记录之
                if (!sliceFlag) {
                    failSliceSet.add(pair);
                }
            }
            // 关闭线程池
            this.executorService.shutdown();
            System.out.println("----<<<<分片读取提交完成");
            // 等切片处理完
            latch.await();
            System.out.println("<<<<----分片读取完成");
            // 当前文件处理完后，将失败的切片放入列表，后面重新计算
            if (!failSliceSet.isEmpty()) {
                // 原文件流关闭，构建新的必要信息，放在处理失败队列
                this.fileFailSliceQueue.offer(new BigFileReader(filePath, failSliceSet));
            }
            // 文件操作完成，及时关闭文件流
            this.randomAccessFile.close();
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归调用计算文件切片
     *
     * @param start
     * @param size
     * @throws IOException
     */
    private void calculateStartEnd(long start, long size) throws IOException {
        if (start > fileLength - 1) {
            return;
        }
        StartEndPair pair = new StartEndPair();
        pair.start = start;
        long endPosition = start + size - 1;
        if (endPosition >= fileLength - 1) {
            pair.end = fileLength - 1;
            startEndPairs.add(pair);
            return;
        }
        // 跳转到指定位置
        randomAccessFile.seek(endPosition);
        byte tmp = (byte) randomAccessFile.read();
        while (tmp != '\n' && tmp != '\r') {
            endPosition++;
            if (endPosition >= fileLength - 1) {
                endPosition = fileLength - 1;
                break;
            }
            randomAccessFile.seek(endPosition);
            tmp = (byte) randomAccessFile.read();
        }
        pair.end = endPosition;
        startEndPairs.add(pair);
        // 递归计算切片
        calculateStartEnd(endPosition + 1, size);
    }

    /**
     * 字符串分解为单词 && 计数
     *
     * @param bytes
     * @param wordCountMap
     */
    private void countWord(byte[] bytes, ConcurrentHashMap<String, AtomicLong> wordCountMap) {
        String line = new String(bytes);
        System.out.println("content is: " + line);
        if (!"".equals(line)) {
            // 使用正则表达式分解单词
            Matcher matcher = Pattern.compile("\\b\\w+\\b").matcher(line);
            while (matcher.find()) {
                String word = matcher.group();
                synchronized (lock) {
                    if (wordCountMap.containsKey(word)) {
                        wordCountMap.get(word).addAndGet(1);
                    } else {
                        wordCountMap.putIfAbsent(word, new AtomicLong(1));
                    }
                }
            }
        }
    }

    /**
     * 文件切片
     */
    private static class StartEndPair {
        public long start;
        public long end;

        @Override
        public String toString() {
            return "star=" + start + ";end=" + end;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (end ^ (end >>> 32));
            result = prime * result + (int) (start ^ (start >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StartEndPair other = (StartEndPair) obj;
            if (end != other.end)
                return false;
            if (start != other.start)
                return false;
            return true;
        }

    }

    /**
     * 声明静态类构建方法
     */
    public static class Builder {
        private File file;
        private int threadSize = 1;
        private int bufferSize = 1024 * 1024;
        private ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue;
        private ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue;

        public Builder(String filePath, ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue, ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue) {
            this.file = new File(filePath);
            if (!this.file.exists()) {
                throw new IllegalArgumentException("文件不存在！");
            }
            this.wordCountQueue = wordCountQueue;
            this.fileFailSliceQueue = fileFailSliceQueue;
        }

        public Builder withThreadSize(int size) {
            this.threadSize = size;
            return this;
        }

        public Builder withBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public BigFileReader build() {
            return new BigFileReader(this.file, this.bufferSize, this.threadSize, this.wordCountQueue, this.fileFailSliceQueue);
        }
    }

    /**
     * 多线程对切片进行计算
     * <p>
     * 1. 实现Callable，返回每个切片是否统计成功，boolean值
     */
    private class SliceReaderTask implements Callable {
        // 起始点
        private long start;
        // 切片大小
        private long sliceSize;
        // buffer大小
        private byte[] readBuff;
        // 计数器
        private CountDownLatch latch;
        // 全局统计队列
        private ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue;
        // 每个切片自己的单词统计器
        private ConcurrentHashMap<String, AtomicLong> sliceWordCountMap;

        public SliceReaderTask(StartEndPair pair, ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue, CountDownLatch latch) {
            this.start = pair.start;
            this.sliceSize = pair.end - pair.start + 1;
            this.readBuff = new byte[bufferSize];
            this.latch = latch;
            this.wordCountQueue = wordCountQueue;
            this.sliceWordCountMap = new ConcurrentHashMap<>();
        }

        /**
         * 返回计算成功还是失败
         *
         * @return
         */
        @Override
        public Object call() {
            ByteArrayOutputStream bos = null;
            try {
                bos = new ByteArrayOutputStream();
                MappedByteBuffer mapBuffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, start, this.sliceSize);
                // 循环遍历切片大小，步长为bufferSize
                for (int offset = 0; offset < sliceSize; offset += bufferSize) {
                    int readLength;
                    if (offset + bufferSize <= sliceSize) {
                        readLength = bufferSize;
                    } else {
                        readLength = (int) (sliceSize - offset);
                    }
                    mapBuffer.get(readBuff, 0, readLength);
                    // 对readBuff 按换行符分隔，分别处理，防止单词被切割
                    for (int i = 0; i < readLength; i++) {
                        byte tmp = readBuff[i];
                        if (tmp == '\n' || tmp == '\r') {
                            countWord(bos.toByteArray(), sliceWordCountMap);
                            bos.reset();
                        } else {
                            bos.write(tmp);
                        }
                    }
                }
                if (bos.size() > 0) {
                    countWord(bos.toByteArray(), sliceWordCountMap);
                }
                System.out.println("slice countMap: " + sliceWordCountMap);
                // 将每个切片成功计算的结果放到队列中
                wordCountQueue.offer(sliceWordCountMap);
                return Boolean.TRUE;
            } catch (Exception e) {
                // 异常处理，将统计失败的切片记录到失败切片队列，专门维护一个线程异步专门处理失败的切片
                e.printStackTrace();
                return Boolean.FALSE;
            } finally {
                // 处理完一个切片，计数器减少一个
                latch.countDown();
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Set<StartEndPair> getStartEndPairs() {
        return startEndPairs;
    }

    public void setStartEndPairs(Set<StartEndPair> startEndPairs) {
        this.startEndPairs = startEndPairs;
    }
}
