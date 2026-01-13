package com.wuming.view;

/**
 * 给定一个纯数字的字符串，要求将其转换为int类型的数据
 *
 * @author che
 * Created on 2025/5/12 17:54
 */
public class Sting2Int {

    public static void main(String[] args) {
        String data = "876";
        int result = parseInt(data);
        System.out.println(result);
    }

    private static int parseInt(String data) {
        if(data == null || data.isEmpty()){
            return 0;
        }
        int result = 0;
        char[] charArray = data.toCharArray();
        for(int i = 0 ; i < charArray.length; i++){
            result = result * 10 + Integer.parseInt(String.valueOf(charArray[i])) ;
        }
        return result;
    }

}
