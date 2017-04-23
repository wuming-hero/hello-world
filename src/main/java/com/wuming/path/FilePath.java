package com.wuming.path;

import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * Created by wuming on 2017/4/19.
 */
public class FilePath {

    String filePath = FilePath.class.getResource("/file/test.json").getPath();
    String filePath2 = FilePath.class.getClassLoader().getResource("file/test.json").getPath();

    /**
     * 通过 clazz.getResource()或 clazz.getClassLoader().getResource()方法获取URL
     *
     * /Users/wuming/workspace/java/hello-world/target/classes/file/test.json
     */
    @Test
    public void test() {
        System.out.println(filePath);
        System.out.println(filePath2);
        File file = new File(filePath);
        System.out.println(file.getName());
    }

    /**
     * file:/Users/wuming/workspace/java/hello-world/target/classes/file/test.json
     * 2种方式获得java 项目的文件路径
     * 1.通过 clazz.getResource("/file");
     * 2.通过 classLoader.getResource("file");
     * 方法2比方法1少了"/"，"/"这个代表“class文件的根目录”。
     */
    @Test
    public void URLTest() {
        URL url = FilePath.class.getResource("/file/test.json");
        System.out.println("resource URL: " + url);

        // 获取classLoader，通过其获取资源路径 3种获取 classLoader 的方法
        url = FilePath.class.getClassLoader().getResource("file/test.json");
        System.out.println("classLoader URL: " + url);

        url = ClassLoader.getSystemClassLoader().getResource("file/test.json");
        System.out.println("classLoader URL: " + url);

        url = Thread.currentThread().getContextClassLoader().getResource("file/test.json");
        System.out.println("classLoader URL: " + url);
    }
}
