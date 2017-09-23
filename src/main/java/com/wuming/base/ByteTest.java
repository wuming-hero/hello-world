package com.wuming.base;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * char是Java中的保留字，与别的语言不同的是，char在Java中是16位的，因为Java用的是Unicode。不过8位的ASCII码包含在Unicode中，是从0~127的。
 * Java中使用Unicode的原因是，Java的Applet允许全世界范围内运行，那它就需要一种可以表述人类所有语言的字符编码--Unicode。
 * 但是English，Spanish，German, French根本不需要这么表示，所以它们其实采用ASCII码会更高效。
 * 因为char是16位的，采取的Unicode的编码方式，所以char就有以下的初始化方式：
 * char c = 'c'; // 字符，可以是汉字，因为是Unicode编码
 * char c = 十进制数，八进制数，十六进制数等等; // 可以用整数赋值
 * char c = \u6700 ; // 用字符的编码值来初始化，如：char='\0',表示结束符，它的ascll码是0，这句话的意思和 char c=0 是一个意思。
 * <p>
 * Created by wuming on 2017/4/25.
 */
public class ByteTest {

    /**
     * java是用unicode来表示字符，"中"这个中文字符的unicode就是2个字节。
     * String.getBytes(encoding)方法是获取指定编码的byte数组表示，通常gbk/gb2312是2个字节，utf-8是3个字节。
     * 如果不指定encoding则取系统默认的encoding。
     *
     * @param args
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "中";
//        str = new String("中");
        char x = '中';
        byte[] bytes = null;
        byte[] bytes1 = null;
        byte[] bytes2 = null;
        try {
            bytes = str.getBytes(); // Mac 系统默认为UTF-8
            bytes1 = str.getBytes("GBK");
            bytes2 = charToBytes(x);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("bytes 大小：" + bytes.length); // 3
        System.out.println("bytes1大小：" + bytes1.length); // 2
        System.out.println("bytes2大小：" + bytes2.length); // 2
        System.out.println(bytes1 == bytes2);
        System.out.println(Objects.equals(str, x));

        System.out.println(byteToChar("c".getBytes("UTF-8")));
    }

    /**
     * char 转换为 byte
     *
     * @param c
     * @return
     */
    public static byte[] charToBytes(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * byte转换为char
     *
     * @param b
     * @return
     */
    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

    /**
     * * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

}
