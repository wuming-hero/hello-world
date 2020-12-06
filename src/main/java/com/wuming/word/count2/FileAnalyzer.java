package com.wuming.word.count2;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单个文件分析器
 * 1. 基于线程数对文件进行切片
 * 2. 每个切片使用 BufferedReder 进行读取
 * 3. 基于Map 实时统计数据
 */
public class FileAnalyzer implements Serializable {

    static final int amount = 10;

    static final String filePath = "/Users/wuming/file_test/";
    /**
     * 声名固定长度的线程池
     */
    static final ExecutorService executorService = Executors.newFixedThreadPool(amount);

    /**
     * 固定大小去读取
     */
    static final char[] READ_CHAR = new char[8096];

    /**
     * 单线程统计各个单词出现的次数
     *
     * @return
     */
    public static void getWordCount(TxtFile txtFile, Map<String, AtomicLong> wordCountMap) {
        File file = new File(filePath + txtFile.getFileName());
        long totalSize = file.length();
        System.out.println("---->>>fileName: " + txtFile.getFileName() + ", fileSize: " + totalSize);
        long size = totalSize / amount;
        long lastSize = totalSize;
        for (int i = 0; i < amount; i++) {
            TxtFileCut txtFileCut;
            if (i == amount - 1) {
                txtFileCut = new TxtFileCut(txtFile.getFileName(), txtFile.getPublishTime(), i * amount, lastSize);
            } else {
                txtFileCut = new TxtFileCut(txtFile.getFileName(), txtFile.getPublishTime(), i * amount, size);
                lastSize -= size;
            }
            System.out.println("---->>>fileName: " + txtFile.getFileName() + ", fileCut: " + txtFileCut);
            // 提交计算
            executorService.submit(() -> {
                getWordCountForTexFileCut(txtFileCut, wordCountMap);
            });
        }
    }

    /**
     * 单线程统计各个单词出现的次数
     *
     * @return
     */
    public static void getWordCountForTexFileCut(TxtFile txtFile, Map<String, AtomicLong> wordCountMap) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(filePath + txtFile.getFileName())));
            int n;
            while ((n = bufferedReader.read(READ_CHAR)) != -1) {
                String content = new String(READ_CHAR, 0, n);
                // 匹配单词
                Matcher matcher = Pattern.compile("\\b\\w+\\b").matcher(content);
                System.out.println("content is: " + content);
                while (matcher.find()) {
                    String word = matcher.group();
                    if (wordCountMap.containsKey(word)) {
                        wordCountMap.get(word).addAndGet(1);
                    } else {
                        wordCountMap.putIfAbsent(word, new AtomicLong(1));
                    }
                }
            }
        } catch (IOException e) {
            // TODO wuming 2020-12-05 22:57 如果异常，使用一个map 维护
            System.out.println("IO exception!!!");
        } finally {
            if (Objects.nonNull(bufferedReader)) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过BufferedReader 实现
     *
     * @param fileCut
     * @param wordCountMap
     */
    public static void getWordCountForTexFileCut(TxtFileCut fileCut, Map<String, AtomicLong> wordCountMap) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(filePath + fileCut.getFileName())));
            bufferedReader.skip(fileCut.getStartPoint());
            // 实际大小
            int actualSize = (int) fileCut.getSize();
            int len;
            while ((len = bufferedReader.read(READ_CHAR)) != -1) {
                if (actualSize - len > 0) {
                    actualSize -= len;
                } else {
                    len = actualSize;
                }
                String content = new String(READ_CHAR, 0, len);
                // 匹配单词
                Matcher matcher = Pattern.compile("\\b\\w+\\b").matcher(content);
                System.out.println("content is: " + content);
                while (matcher.find()) {
                    String word = matcher.group();
                    if (wordCountMap.containsKey(word)) {
                        wordCountMap.get(word).addAndGet(1);
                    } else {
                        wordCountMap.putIfAbsent(word, new AtomicLong(1));
                    }
                }
            }
        } catch (IOException e) {
            // TODO wuming 2020-12-05 22:57 如果异常，使用一个map 维护
            System.out.println("IO exception!!!");
        } finally {
            if (Objects.nonNull(bufferedReader)) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过RandomAccessFile 实现
     *
     * @param fileCut
     * @param wordCountMap
     */
    public static void getWordCountForTexFileCut2(TxtFileCut fileCut, Map<String, AtomicLong> wordCountMap) {
        try {
            File file = new File(filePath + fileCut.getFileName());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(fileCut.getStartPoint());
            // 实际大小
            int actualSize = (int) (fileCut.getEndPoint() - fileCut.getStartPoint());
            byte[] bytes = new byte[1024];
            int len;
            while ((len = randomAccessFile.read(bytes)) != -1) {
                if (actualSize - len > 0) {
                    actualSize -= len;
                } else {
                    len = actualSize;
                }
                String content = new String(bytes, 0, len);
                // 匹配单词
                Matcher matcher = Pattern.compile("\\b\\w+\\b").matcher(content);
                System.out.println("content is: " + content);
                while (matcher.find()) {
                    String word = matcher.group();
                    if (wordCountMap.containsKey(word)) {
                        wordCountMap.get(word).addAndGet(1);
                    } else {
                        wordCountMap.putIfAbsent(word, new AtomicLong(1));
                    }
                }
            }
        } catch (IOException e) {
            // TODO wuming 2020-12-05 22:57 如果异常，使用一个map 维护
            System.out.println("IO exception!!!");
        }
    }

}
