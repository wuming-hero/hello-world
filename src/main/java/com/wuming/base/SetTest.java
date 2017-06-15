package com.wuming.base;

import org.junit.Test;

import java.util.*;

/**
 * Created by wuming on 2017/5/2.
 */
public class SetTest {

    /**
     * 很神奇的，这边的HashSet 总是
     */
    @Test
    public void test(){
        HashSet<Integer> set = new HashSet<>();
        set.add(3);
        set.add(2);
        set.add(4);
        set.add(1);
        set.add(6);
        System.out.println("set: " + set);

        SortedSet<Integer> sortSet = new TreeSet<>();
        sortSet.add(2);
        sortSet.add(1);
        sortSet.add(3);
        sortSet.add(1);
        System.out.println("sort set: " + sortSet);

    }
}
