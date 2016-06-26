package com.wuming.junit;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wuming on 16/6/26.
 */
public class JUnitDemoTest {

    private JUnitDemo jUnitDemo = new JUnitDemo();

    @Test
    public void add() throws Exception {
        double result = jUnitDemo.add(1, 2);
        Assert.assertEquals("加法有问题", 3, result, 0);
    }

    @Test
    public void minus() throws Exception {
        double result = jUnitDemo.minus(3, 2);
        Assert.assertEquals("减法有问题", 1, result, 0);
    }

    @Test(expected = ArithmeticException.class)
    public void divide() throws Exception {
        double result = jUnitDemo.divide(2, 2);
        Assert.assertEquals("除法有问题", 1, result, 0);
    }

    @Test(timeout = 100)
    public void mul() throws Exception {
        double result = jUnitDemo.mul(3, 5);
        Thread.sleep(150);
        Assert.assertEquals("乘法计算有误", 14, result, 0);
    }

}