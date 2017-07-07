package com.wuming.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuming on 2017/7/7.
 * Guava 中 不可变集合相关使用实例
 * Immutable集合使用方法：
 * 　　一个immutable集合可以有以下几种方式来创建：
 * 　　1.用copyOf方法, 譬如, ImmutableSet.copyOf(set)
 * 　　2.使用of方法，譬如，ImmutableSet.of("a", "b", "c")或者ImmutableMap.of("a", 1, "b", 2)
 * 　　3.使用Builder类
 */
public class Immutable {


    @Test
    public void copyOfTest() {
        ImmutableSet<String> immutableSet = ImmutableSet.of("peida", "jerry", "harry", "lisa");
        System.out.println("immutableSet：" + immutableSet);

        ImmutableList<String> immutableList = ImmutableList.copyOf(immutableSet);
        System.out.println("immutableList：" + immutableList);

        ImmutableSortedSet<String> immutableSortedSet = ImmutableSortedSet.copyOf(immutableSet);
        System.out.println("imSortSet：" + immutableSortedSet);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i + "x");
        }
        System.out.println("list：" + list);

        ImmutableList<String> immutableList1 = ImmutableList.copyOf(list.subList(2, 18));
        System.out.println("immutableList1：" + immutableList1);

        int size = immutableList1.size();
        System.out.println("size：" + size);

        ImmutableSet<String> immutableSet1 = ImmutableSet.copyOf(immutableList1.subList(2, size - 3));
        System.out.println("immutableSet1：" + immutableSet1);
    }

    /**
     * of() 与 builder() 方法实现
     */
    @Test
    public void immutableTest() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println("list：" + list);

        ImmutableList<String> immutableList = ImmutableList.copyOf(list);
        System.out.println("immutableList：" + immutableList);

        ImmutableList<String> immutableOfList = ImmutableList.of("peida", "jerry", "harry");
        System.out.println("immutableOfList：" + immutableOfList);

        ImmutableSortedSet<String> immutableSortedSet = ImmutableSortedSet.of("a", "b", "c", "a", "d", "b");
        System.out.println("immutableSortedSet：" + immutableSortedSet);

        list.add("baby");
        System.out.println("list add a item after list:" + list);
        System.out.println("list add a item after immutableList:" + immutableList);

        // 使用builder() 来构建
        ImmutableSet<Color> immutableColorSet =
                ImmutableSet.<Color>builder()
                        .add(new Color(0, 255, 255))
                        .add(new Color(0, 191, 255))
                        .build();

        ImmutableList<Integer> immutableList1 = ImmutableList.<Integer>builder().add(1).add(2).build();
        System.out.println("immutableList create by builder:" + immutableList1);

        System.out.println("immutableColorSet:" + immutableColorSet);
    }
}
