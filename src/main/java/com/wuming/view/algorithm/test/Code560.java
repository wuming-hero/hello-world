package com.wuming.view.algorithm.test;


import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子串
 * <p>
 * 给你一个整数数组 nums 和一个整数 k ，请你统计并返回 该数组中和为 k 的子数组的个数 。
 * <p>
 * 子数组是数组中元素的连续非空序列。
 */
public class Code560 {

    public static void main(String[] args) {
        int nums[] = new int[]{1, 1, 1};
        int k = 3;
        System.out.println(subarraySum(nums, k));
    }

    /**
     * 使用前缀和的方法可以解决这个问题，因为我们需要找到和为k的连续子数组的个数。通过计算前缀和，我们可以将问题转化为求解两个前缀和之差等于k的情况。
     * <p>
     * 1. 假设数组的前缀和数组为prefixSum，其中prefixSum[i]表示从数组起始位置到第i个位置的元素之和。那么对于任意的两个下标i和j（i < j），如果prefixSum[j] - prefixSum[i] = k，即从第i个位置到第j个位置的元素之和等于k，那么说明从第i+1个位置到第j个位置的连续子数组的和为k。
     * <p>
     * 2. 通过遍历数组，计算每个位置的前缀和，并使用一个哈希表来存储每个前缀和出现的次数。
     * 在遍历的过程中，我们检查是否存在prefixSum[j] - k的前缀和，如果存在，说明从某个位置到当前位置的连续子数组的和为k，我们将对应的次数累加到结果中。
     * <p>
     * 这样，通过遍历一次数组，我们可以统计出和为k的连续子数组的个数，并且时间复杂度为O(n)，其中n为数组的长度。
     *
     * @param nums 输入的整数数组
     * @param k    要查找的子数组的和
     * @return 具有和为 k 的子数组的个数
     */
    public static int subarraySum(int[] nums, int k) {
        int count = 0;
        int sum = 0;
        // 哈希表来存储每个前缀和出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1); // 初始化前缀和为0的次数为1

        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (map.containsKey(sum - k)) {
                count += map.get(sum - k);
            }
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }

        return count;
    }


}
