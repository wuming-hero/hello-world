package com.wuming.file;

import org.junit.Test;

import java.io.*;

/**
 * Created by wuming on 2017/4/24.
 */
public class FileWrite {
    String path = FileWrite.class.getResource("/file/fileWrite.txt").getPath();

    @Test
    public void writeBytesTest() {
        System.out.println("path: " + path);
        String text = "我是测试文本";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter isr = new OutputStreamWriter(fos);
            byte[] writeBytes = text.getBytes();
            fos.write(writeBytes);
//            fos.write(writeBytes, 0, writeBytes.length);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件追加1
     */
    @Test
    public void appendFileTest1() {
        String content = "我是追加的内容。";
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            long fileLength = raf.length();
            raf.seek(fileLength);
//            raf.writeBytes(conent); // 乱码
            raf.write(content.getBytes("UTF-8"));
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件追加2
     */
    @Test
    public void appendFileTest2() {
        String content = "我是追加的内容2。";
        try {
            // FileWrite 的第二个构造参数表示 追加
            FileWriter fileWriter = new FileWriter(path, true);
            fileWriter.append(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
