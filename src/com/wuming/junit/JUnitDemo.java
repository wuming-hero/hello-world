package com.wuming.junit;

import org.junit.Test;

/**
 * Created by wuming on 16/6/26.
 */

/**
 * 单元测试的最基本的一个功能是能进行自动化测试。单元测试都是通过断言的方式来确定结果是否正确，即使用Assert
 * 一般是junit4测试, 需要引入 junit-4.x.jar 包
 */
public class JUnitDemo {

    public double add(double a, double b){
        return a + b;
    }

    public double minus(double a, double b){
        return a - b;
    }

    public double divide(double a, double b){
        return a / b;
    }

    public double mul(double a , double b){
        return a * b;
    }
}
