package com.wuming.stream;

import java.util.function.Supplier;

/**
 * 自然数集合的规则非常简单，每个元素都是前一个元素的值+1，因此，自然数发生器用代码实现如下
 * 反复调用get()，将得到一个无穷数列，利用这个Supplier，可以创建一个无穷的Stream
 *
 * @author wuming
 * Created on 2018/3/19 10:37
 */
public class NaturalSupplier implements Supplier<Long> {

    long value = 0;

    @Override
    public Long get() {
        this.value = this.value + 1;
        return this.value;
    }
}
