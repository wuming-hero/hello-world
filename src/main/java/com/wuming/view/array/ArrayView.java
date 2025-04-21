package com.wuming.view.array;

import java.util.*;

/**
 * 笔试面试题
 * <p>
 * 1、有一个非常大的数组，其中某一个数字的出现次数达到了这个数组长度的一半以上，请问如何找到这个数字？并说明一下其时间空间复杂度是多少。
 *
 * @author wuming
 * Created on 2020-11-18 21:43
 */
public class ArrayView {

    /**
     * 摩尔投票法（Moore's Voting Algorithm）：时间复杂度：O(n)
     * 摩尔投票法的核心思想是通过抵消不同元素的方式，最终剩下的元素即为目标元素。其原理基于以下事实：
     * 如果一个元素是主元素（出现次数超过一半），那么在抵消过程中，其他元素的总数不足以将其计数器减为零，最终剩下的必然是主元素。
     * <p>
     * 具体实现：
     * 遍历数组,对于每个元素：
     * 1. 如果计数器为0，将当前元素设为候选元素，并将计数器设为1。
     * 2. 否则：如果当前元素与候选元素相同，计数器加1;否则，计数器减1。
     * <p>
     * 时间复杂度：O(n) 遍历数组两次（一次投票，一次验证，但验证可选）。对于非常大的数组，这非常高效。
     * 空间复杂度：O(1) 仅使用常数级别的额外空间（存储候选元素和计数器）
     *
     * @param nums
     * @return
     */
    public int findMajorityElement(int[] nums) {
        // 候选元素
        int candidate = 0;
        // 计数器
        int count = 0;

        for (int num : nums) {
            // 如果计数器为0，将当前元素设为候选元素，并将计数器设为1。
            if (count == 0) {
                candidate = num;
            }
            count += (num == candidate) ? 1 : -1;
        }
        return candidate;
    }


    /**
     * 排序法：
     * 将数组排序后，中间位置的元素必然是主元素。
     * <p>
     * 时间复杂度：O(nlogn)（排序的时间开销）
     * 空间复杂度：O(1)
     * <p>
     * 缺点：时间复杂度较高，不适合超大规模数据。
     *
     * @param dataArray
     * @return
     */
    public int getMaxAmountValue2(Integer[] dataArray) {
        // 1. 排序
        Arrays.sort(dataArray);
        // 2. 中间的元素即为主元素
        int halfAmount = dataArray.length / 2;
        return dataArray[halfAmount];
    }

    /**
     * 哈希表统计法:
     * 遍历数组，用哈希表统计每个元素的出现次数，记录超过半数的元素。
     * <p>
     * 时间复杂度 : O(n)
     * 空间复杂度 : O(n)
     * <p>
     * 缺点：空间复杂度较高，不适合非常大的数组。
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


}
