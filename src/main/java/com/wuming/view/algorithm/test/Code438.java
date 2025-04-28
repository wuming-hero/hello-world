package com.wuming.view.algorithm.test;



import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 滑动窗口
 *
 * 找到字符串中所有字母异位词
 *
 * 给定两个字符串 s 和 p，找到 s 中所有 p 的 异位词 的子串，返回这些子串的起始索引。不考虑答案输出的顺序。
 *
 *
 * 示例 1:
 *
 * 输入: s = "cbaebabacd", p = "abc"
 * 输出: [0,6]
 * 解释:
 * 起始索引等于 0 的子串是 "cba", 它是 "abc" 的异位词。
 * 起始索引等于 6 的子串是 "bac", 它是 "abc" 的异位词。
 *  示例 2:
 *
 * 输入: s = "abab", p = "ab"
 * 输出: [0,1,2]
 * 解释:
 * 起始索引等于 0 的子串是 "ab", 它是 "ab" 的异位词。
 * 起始索引等于 1 的子串是 "ba", 它是 "ab" 的异位词。
 * 起始索引等于 2 的子串是 "ab", 它是 "ab" 的异位词。
 */
public class Code438 {

    public static void main(String[] args) {
        String s = "abab";
       String  p = "ab";
        System.out.println(findAnagrams(s, p));
    }

    public static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = Lists.newArrayList();
//        int left = 0;
        // 排序后的字符串，方便比较
        char[] chars = p.toCharArray();
        Arrays.sort(chars);
        String sortP = String.valueOf(chars);

        for (int i = 0; i < s.length(); i++) {
            if (p.contains(String.valueOf(s.charAt(i)))) {

                int endIndex =  i + p.length();

                if(endIndex <= s.length()) {
                    String target = s.substring(i, endIndex);
                    char[] targetArray = target.toCharArray();
                    Arrays.sort(targetArray);
                    String sortTarget = String.valueOf(targetArray);
                    // 相同，则记录下来
                    if(sortP.equals(sortTarget)) {
                        result.add(i);
                    }
                }

            }
        }
        return result;
    }

    // TODO manji 2025/4/25 11:47 没看懂，后期再看
    public List<Integer> findAnagrams2(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        int[] cnt = new int[26]; // 统计 p 的每种字母的出现次数
        for (char c : p.toCharArray()) {
            cnt[c - 'a']++;
        }
        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            int c = s.charAt(right) - 'a';
            cnt[c]--; // 右端点字母进入窗口
            while (cnt[c] < 0) { // 字母 c 太多了
                cnt[s.charAt(left) - 'a']++; // 左端点字母离开窗口
                left++;
            }
            if (right - left + 1 == p.length()) { // s' 和 p 的每种字母的出现次数都相同
                ans.add(left); // s' 左端点下标加入答案
            }
        }
        return ans;
    }


}
