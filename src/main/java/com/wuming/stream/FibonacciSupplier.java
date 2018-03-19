package com.wuming.stream;

import java.util.function.Supplier;

/**
 * @author wuming
 * Created on 2018/3/19 10:46
 */
public class FibonacciSupplier implements Supplier<Long> {
    long a = 0;
    long b = 1;

    @Override
    public Long get() {
        long tem = a + b;
        a = b;
        b = tem;
        return tem;
    }
}
