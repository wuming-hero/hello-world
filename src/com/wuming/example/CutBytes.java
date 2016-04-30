package com.wuming.example;

/**
 * Created by wuming on 16/4/30.
 */
public class CutBytes {

    /**
     * 写一个方法  String left(String str ,int n) str字符串中可能包含中文，中文是2bytes，实现的功能是
     * 如：“中abc12” n=4  则该方法返回“中ab”  “中abc国a” n=6 则返回“中abc”中文是一半时不返回
     */

    public static String left(String str, int n) {
        char[] chArr = str.toCharArray();
        int i = 0, len = 0;
        while (i < chArr.length) {
            if (chArr[i] > 255) {
                len += 2;
            } else {
                len++;
            }
            if (len >= n) {
                break;
            }
            i++;
        }
        return str.substring(0, i);
    }


    /**
     * utf-8 的汉字占3个字节  gbk的是2个字节
     * @param str
     * @param n
     * @return
     */
    public static String getStr(String str, int n) {
        String s;
        int i;
        for (i = 0; i < str.length(); i++) {
            s = String.valueOf(str.charAt(i));
            byte[] sByte = s.getBytes();
            if (sByte.length == 1) {
                n = n - 1;
            } else if (sByte.length == 2 || sByte.length == 3) {
                n = n - 2;
            }
            if (n < 0) {
                break;
            }
        }
        str = str.substring(0, i);
        return str;
    }

    public static void main(String[] args) {
        System.out.println(left("中abc12", 4));
        System.out.println(left("中abc国a", 6));
        System.out.println(getStr("中abc12", 4));
        System.out.println(getStr("中abc国a", 6));
    }

}
