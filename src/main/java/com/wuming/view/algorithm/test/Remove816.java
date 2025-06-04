package com.wuming.view.algorithm.test;

import java.util.Stack;

/**
 * 消消乐
 * <p>
 * 给定字符串："88188161661"，消除连续的816，直至没有连续的816出现。
 * 输出：81
 *
 * @author che
 * Created on 2025/5/30 12:49
 */
public class Remove816 {

    public static void main(String[] args) {
        System.out.println(removeConsecutive816("88188161661")); // 输出: 81
    }

    /**
     * 基于栈实现
     *
     * @param input
     * @return
     */
    public static String removeConsecutive816(String input) {
        Stack<Character> stack = new Stack<>();
        for (char c : input.toCharArray()) {
            stack.push(c);
            // 检查栈顶是否构成"816"
            if (stack.size() >= 3
                    && stack.get(stack.size() - 3) == '8'
                    && stack.get(stack.size() - 2) == '1'
                    && stack.get(stack.size() - 1) == '6') {
                // 弹出这三个字符
                stack.pop();
                stack.pop();
                stack.pop();
            }
        }

        // 将栈中剩余字符转为字符串
        StringBuilder result = new StringBuilder();
        for (char c : stack) {
            result.append(c);
        }
        return result.toString();
    }


}
