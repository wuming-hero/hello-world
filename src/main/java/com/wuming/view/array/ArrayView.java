package com.wuming.view.array;

import java.util.*;

/**
 * 笔试面试题
 *
 * @author wuming
 * Created on 2020-11-18 21:43
 */
public class ArrayView {

    /*
     1、有一个非常大的数组，其中某一个数字的出现次数达到了这个数组长度的一半以上，请问如何找到这个数字？并说明一下其时间空间复杂度是多少。
     */
    /**
     * 声明一个计数的Map,key为数字，value为数字出现的次数
     * 时间复杂度 < O(n)
     */
    public int getMaxAmountValue(Integer[] dataArray) {
        Map<Integer, Integer> countMap = new HashMap<>();
        int halfAmount = dataArray.length / 2;
        for (int i = 0; i < dataArray.length; i++) {
            if (countMap.containsKey(dataArray[i])) {
                // 如果已存在，数量+1
                Integer curAmount = countMap.get(dataArray[i]) + 1;
                if (curAmount > halfAmount) {
                    return dataArray[i];
                }
                countMap.put(dataArray[i], curAmount);
            } else {
                // 如果不存在，计数为1
                countMap.put(dataArray[i], 1);
            }
        }
        return 0;
    }

    /**
     * 简单解法2
     *
     * @param dataArray
     * @return
     */
    public int getMaxAmountValue2(Integer[] dataArray) {
        int halfAmount = dataArray.length / 2;
        return dataArray[halfAmount];
    }



}
