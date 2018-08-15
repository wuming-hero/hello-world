package com.wuming.stream;

import org.junit.Test;

import java.io.*;

/**
 * @author wuming
 * Created on 2018/8/15 22:07
 */
public class DataStreamTest {

    @Test
    public void test() {
        // 使用DataInputStream,DataOutputStream写入文件且从文件中读取数据。
        try {
            String filePath = this.getClass().getResource("/file/datasteam.txt").getPath();
            System.out.println("filePath: " + filePath);
            // Data Stream写到输入流中
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));
            dos.writeBytes("世界"); // 按2字节写入，都是写入的低位
            dos.writeChars("世界"); // 按照Unicode写入
            // 按照UTF-8写入(UTF8变长，开头2字节是由writeUTF函数写入的长度信息，方便readUTF函数读取)
            dos.writeUTF("世界");
            dos.flush();
            dos.close();

            // Data Stream 读取
            DataInputStream dis = new DataInputStream(new FileInputStream(filePath));

            // 读取字节
            byte[] b = new byte[2];
            dis.read(b);
            System.out.println(new String(b, 0, 2)); // 乱码 两个汉字都只被写入了低位，因此肯定乱码。

            // 读取字符
            char[] c = new char[2];
            for (int i = 0; i < 2; i++) {
                c[i] = dis.readChar();
            }
            System.out.println(new String(c, 0, 2)); // 世界

            // 读取UTF
            System.out.println(dis.readUTF()); // 世界

            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用writeUTF和readUTF可以免去程序猿对于长度的考虑，
     * 因为这两个函数为我们解决了长度问题，写入了文件，因此我们无需关心。但这样就使用了额外的空间去记录长度信息。
     * 会使用2字节记录UTF-8字符串的长度信息
     */
    @Test
    public void test2() {
        // 使用DataInputStream,DataOutputStream写入文件且从文件中读取数据。
        try {
            String filePath = this.getClass().getResource("/file/datasteam.txt").getPath();
            // Data Stream写到输入流中
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));
            dos.write("世界".getBytes()); // 按UTF8编码(我的系统默认编码方式)写入
            //dos.write("世界".getBytes("GBK"));  //指定其他编码方式
            dos.writeChars("世界"); // 按照Unicode写入
            // 按照UTF-8写入(UTF8编码长度可变，开头2字节是由writeUTF函数写入的长度信息，方便readUTF函数读取)
            dos.writeUTF("世界");
            dos.flush();
            dos.close();

            // Data Stream 读取
            DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
            // 读取字节
            byte[] b = new byte[6];
            dis.read(b);
            // 读取前6字节并按照UTF-8解码就可以避免乱码
            System.out.println(new String(b, 0, 6));  // 世界

            // 读取字符
            char[] c = new char[2];
            for (int i = 0; i < 2; i++) {
                c[i] = dis.readChar();
            }
            System.out.println(new String(c, 0, 2)); // 世界

            // 读取UTF
            System.out.println(dis.readUTF()); // 世界

            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
