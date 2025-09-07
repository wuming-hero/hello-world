package com.wuming.view2;

/**
 * @author che
 * Created on 2025/5/12 17:54
 */
public class Sort {


    public static void main(String[] args) {
        String data = "876";
        int result = parseInt(data);
        System.out.println(result);
    }

    private static int parseInt(String data) {
        if(data == null || data.length() == 0){
            return 0;
        }
        int result = 0;
        char[] charArray = data.toCharArray();
        for(int i = 0 ; i < charArray.length; i++){
            result = Integer.parseInt(String.valueOf(charArray[i])) +   result * 10;
        }
        return result;
    }


}
