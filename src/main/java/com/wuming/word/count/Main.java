package com.wuming.word.count;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    // TODO wuming 2020-12-06 15:55 默认指定文件路径
    private static final String dirPath = "/Users/wuming/file_test";

    /**
     * 10个线程提交处理文件
     */
    private static final ExecutorService handlerFileService = Executors.newFixedThreadPool(10);
    /**
     * 2个线程去合并数据
     */
    private static final ExecutorService mergeDataService = Executors.newFixedThreadPool(2);
    /**
     * 2个线程去处理之前处理失败的切片
     */
    private static final ExecutorService failSliceService = Executors.newFixedThreadPool(2);
    /**
     * 数据统计队列,每一个元素为单个文件切片的数据统计
     */
    private static final ConcurrentLinkedQueue<ConcurrentHashMap<String, AtomicLong>> wordCountQueue = new ConcurrentLinkedQueue<>();
    /**
     * 总计数器Map
     */
    private static final ConcurrentHashMap<String, AtomicLong> wordCountMap = new ConcurrentHashMap<>();
    /**
     * 待处理文件队列
     */
    private static final ConcurrentLinkedQueue<String> filePathQueue = new ConcurrentLinkedQueue<>();
    /**
     * 失败文件队列
     */
    private static final ConcurrentLinkedQueue<BigFileReader> fileFailSliceQueue = new ConcurrentLinkedQueue<>();

    /**
     * 监听文件变化 ，如果有新文件，放入filePathQueue
     */
    public static void listenNewFile() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                WatchKey key;
                try {
                    WatchService watchService = FileSystems.getDefault().newWatchService();
                    Paths.get(dirPath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                    while (true) {
                        // path为监听文件夹
//                        File file = new File(dirPath);
//                        File[] files = file.listFiles();
                        System.out.println("等待加载新文件...");
                        key = watchService.take();//没有文件增加时，阻塞在这里
                        for (WatchEvent<?> event : key.pollEvents()) {
                            String filePath = dirPath + "/" + event.context();
                            System.out.println("新增加的文件路径: " + filePath);
//                            File file1 = files[files.length - 1];//获取最新文件
                            String ext = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
                            if (Objects.equals(ext, "txt")) {
                                // 如果有新文件，将文件路径放入队列中
                                filePathQueue.offer(filePath);
                            } else {
                                System.out.println("---------------新增加的文件格式不正确，忽略不统计-----------");
                            }
                        }
                        if (!key.reset()) {
                            break; //中断循环
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000, 3000);//第一个数字2000表示，2000ms以后开启定时器,第二个数字3000，表示3000ms后运行一次run
    }

    /**
     * 主进程，入口及各线程调用
     * <p>
     * 1. 初始化全局文件队列，将指定路径下所有文件路径放入队列
     * 2. 监听指定路径下文件变化事件，如果有新文件，放入全局文件队列
     * 3.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // 接收目录参数和扩展名
        Path fileTree = Paths.get(dirPath);
        // 根据电子书文本
        Searcher walk = new Searcher("*.txt");
        // 查找该目录下所有txt文件
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(fileTree, opts, Integer.MAX_VALUE, walk);
        ArrayList<String> filePathList = walk.getFilePaths();
        // 所有的文件路径放入队列中，如果有新的也放入队列中
        for (String filePath : filePathList) {
            filePathQueue.offer(filePath);
        }
        // 监听新文件变化
        listenNewFile();

        // 多线程去处理文件
        handlerFileService.execute(new FileQueueConsumer(filePathQueue, wordCountQueue, wordCountMap, fileFailSliceQueue));
        handlerFileService.shutdown();

        // 多线程去合并数据到主统计Map
        mergeDataService.execute(new WordCountQueueConsumer(wordCountQueue, wordCountMap));
        mergeDataService.shutdown();

        // 多线程处理之前处理异常的切片信息
        failSliceService.execute(new FileFailSliceQueueConsumer(fileFailSliceQueue, wordCountQueue, fileFailSliceQueue));
        failSliceService.shutdown();
    }

}
