package com.wuming.word.count2;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class WordCounter {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        // 接收目录参数和扩展名
        Path fileTree = Paths.get("/Users/wuming/file_test");
        // 根据电子书文本
        Searcher walk = new Searcher("*.txt");

        // 查找该目录下所有txt文件
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(fileTree, opts, Integer.MAX_VALUE, walk);
        ArrayList<String> filePathList = walk.getFilePaths();

        // 解析每个文件的单词

        Map<String, AtomicLong> wordCountMap = new ConcurrentHashMap<>();
        TxtFile txtFile;
        for (String filePath : filePathList) {
            // 声名每本书的维护
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            txtFile = new TxtFile(fileName);
            FileAnalyzer.getWordCount(txtFile, wordCountMap);
//            FileAnalyzer.getWordCountForTexFileCut(txtFile, wordCountMap);
        }

        System.out.println(wordCountMap);
    }


}
