package com.wuming.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Created by wuming on 2017/4/23.
 * java 文件相关的操作 读、写
 */
public class FileRead {

    private String path = null;

    @Before
    public void init() {
        path = FileRead.class.getResource("/file/file.txt").getPath();
    }

    @After
    public void destroy() {
        System.out.println("----after test----");
    }

    /**
     * 一个字符一个字符的读取
     * 使用 FileReader 类实现
     * <p>
     * 其中read()方法返回的是读取得下个字符。
     * 当然你也可以使用read(char[] ch,int off,int length)这和处理二进制文件的时候类似。
     * 事实上在FileReader中的方法都是从InputStreamReader中继承过来的。read()方法是比较费时间的
     */
    @Test
    public void readFileByChar() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
            StringBuffer sb = new StringBuffer();
            int ch;
            // isr.read() 返回读取的字符，逐个字符读取 效率低 可以一次读取一上字符数组
            while ((ch = fileReader.read()) != -1) {
                // 对于windows下，rn这两个字符在一起时，表示一个换行。 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉r，或者屏蔽n。否则，将会多出很多空行。
                if ((char) ch != 'r') {
                    sb.append((char) ch);
                }
            }
            fileReader.close();
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("----file input stream reader----");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fis);
            StringBuffer sb = new StringBuffer();
            char[] chars = new char[10]; // 一次读取10个字符
            int length;
            // isr.read(char[]) 返回读取的数组长度
            while ((length = isr.read(chars)) != -1) {
                sb.append(chars, 0, length);
            }
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 逐行读取文件，适用于文本
     * 使用 BufferedFileReader 类实现
     * <p>
     * 为了提高效率，使用BufferedReader对Reader进行包装，
     * 这样可以提高读取得速度，我们可以一行一行的读取文本，使用readLine()方法
     */
    @Test
    public void readFileByLine() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件
     * 可以用来读取英文文本，不可以读取含有中文的文本，否则出现乱码。
     * 中文使用 unicode 编码，一个中文字符占用2个字节(GBK、GB3122)或3个字节(编码集为utf-8),
     * 假如含有中文的文本不是很大，可以使用 fis.read(byte[2048])，一次将全部文本内容读取出来，这样不会出现乱码。
     */
    @Test
    public void readFileByBytes() {
        FileInputStream fis = null;
        try {
            // // 读取中文乱码(I am wuming. 20ä¸çºª90)，可以使用 InputStreamReader 包装 FileInputStream
            fis = new FileInputStream(path);
            StringBuffer sb = new StringBuffer();
            int readByte = 0;
            // fis.read() 返回一个 byte
            while ((readByte = fis.read()) != -1) {
                sb.append((char) readByte);
            }
            fis.close();
            System.out.println("逐个字节读取文件: " + sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis = new FileInputStream(path);
            byte[] bytes = new byte[10];
            int readByte = 0;
            StringBuffer sb = new StringBuffer();
            System.out.println("当前字节输入流中的字节数为:" + fis.available());
            while ((readByte = fis.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, readByte, "UTF-8"));
            }
            System.out.println(sb);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 随机读取文件
     * seek()方法使用
     * <p>
     * RandomAccessFile
     */
    @Test
    public void randomReadFile() {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(path, "r");
            long fileLength = raf.length();
            int beginIndex = fileLength > 10 ? 10 : 0;
            byte[] bytes = new byte[10];
            raf.seek(beginIndex); // 使用 seek 将读文件的开始位置移到beginIndex位置。
            int byteRead;
            while ((byteRead = raf.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteRead);
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
