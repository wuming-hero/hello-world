package com.wuming.view;

import java.util.Stack;

/**
 * 给定一个整数数组，需要找到一个波峰（局部最大值）的下标，要求时间复杂度为 O(log n)。
 * <p>
 * 波峰定义为：
 * <p>
 * 对于非边界元素，满足 arr[i] >= arr[i-1] 且 arr[i] >= arr[i+1]。
 * 对于左边界元素（i=0），满足 arr[0] >= arr[1]（如果数组长度大于1）。
 * 对于右边界元素（i=n-1），满足 arr[n-1] >= arr[n-2]（如果数组长度大于1）。
 * <p>
 * 可以通过二分查找算法实现，因为数组虽然不是完全有序，但可以利用局部单调性来缩小搜索范围。
 * 每次迭代检查中间元素，并根据其邻居值决定搜索左半部分或右半部分。
 *
 * @author che
 * Created on 2025/5/21 19:57
 */
public class FindPeak {

    public static void main(String[] args) {
        int[] arr = {1, 4, 3, 4, 5, 6, 7, 20, 9, 10};
        System.out.println(findPeak(arr));
        System.out.println(findPeak2(arr));

    }

    /**
     * 查找波峰
     * <p>
     * 只需要比较中间元素与其右侧邻居（如果存在），根据比较结果缩小搜索范围。
     * 这种写法更简洁，同时仍保持 O(log n) 的时间复杂度。
     *
     * @param arr
     * @return
     */
    private static int findPeak(int[] arr) {
        // 边界值处理
        if (arr.length == 0) {
            return -1;
        }
        if (arr.length == 1) {
            return 0;
        }
        if (arr.length == 2) {
            return arr[0] >= arr[1] ? 0 : 1;
        }


        int low = 0;
        int high = arr.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] < arr[mid + 1]) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    /**
     * 线性扫描
     * <p>
     * 时间复杂度：（O(n)）。
     *
     * 借助stack 或直接 for循环
     *
     * @param arr
     * @return
     */
    private static int findPeak2(int[] arr) {
// 边界值处理
        if (arr.length == 0) {
            return -1;
        }
        if (arr.length == 1) {
            return 0;
        }
        if (arr.length == 2) {
            return arr[0] >= arr[1] ? 0 : 1;
        }

        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < arr.length; i++) {
            stack.push(arr[i]);
            if (stack.size() >= 3) {
                int left = stack.get(stack.size() - 3);
                int peak = stack.get(stack.size() - 2);
                int right = stack.get(stack.size() - 1);
                if (peak > left && peak > right) {
                    return i - 1;
                }
            }
        }
        return -1;
    }

}
