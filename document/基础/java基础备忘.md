## 容器
Java 集合，也叫作容器，主要是由两大接口派生而来：
一个是 Collection接口，主要用于存放单一元素；
另一个是 Map 接口，主要用于存放键值对。
对于Collection 接口，下面又有三个主要的子接口：List、Set 、Queue。

![图片2](../../src/main/resources/static/image/base/container.png)


## 语法糖
语法糖（Syntactic sugar） 代指的是编程语言为了方便程序员开发程序而设计的一种特殊语法，这种语法对编程语言的功能并没有影响。
实现相同的功能，基于语法糖写出来的代码往往更简单简洁且更易阅读。
举个例子，Java 中的 for-each 就是一个常用的语法糖，其原理其实就是基于普通的 for 循环和迭代器。

```java
String[] strs = {"JavaGuide", "公众号：JavaGuide", "博客：https://javaguide.cn/"};
for (String s : strs) {
    System.out.println(s);
}
```

不过，JVM 其实并不能识别语法糖，Java 语法糖要想被正确执行，需要先通过编译器进行解糖，也就是在程序编译阶段将其转换成 JVM 认识的基本语法。
这也侧面说明，`Java 中真正支持语法糖的是 Java 编译器而不是 JVM` 。
如果你去看`com.sun.tools.javac.main.JavaCompiler`的源码，你会发现在`compile()`中有一个步骤就是调用`desugar()`，这个方法就是负责解语法糖的实现的。

### Java 中有哪些常见的语法糖？
Java 中最常用的语法糖主要有泛型、自动拆装箱、变长参数、枚举、内部类、增强 for 循环、try-with-resources 语法、lambda 表达式等。

java 语法糖 https://javaguide.cn/java/basis/syntactic-sugar.html

## Atomic 类
AtomicInteger是 Java 的原子类之一，主要用于对 int 类型的变量进行原子操作， 它利`用Unsafe类提供的低级别原子操作方法实现无锁的线程安全性` 。

可以看到，getAndAddInt 使用了 do-while 循环：在compareAndSwapInt操作失败时，会不断重试直到成功。
也就是说，getAndAddInt方法会通过 compareAndSwapInt 方法来尝试更新 value 的值，如果更新失败（当前值在此期间被其他线程修改），它会重新获取当前值并再次尝试更新，直到操作成功。
由于 CAS 操作可能会因为并发冲突而失败，因此通常会与while循环搭配使用，在失败后不断重试，直到操作成功。这就是`自旋锁` 机制 。

```java
// 获取 Unsafe 实例
private static final Unsafe unsafe = Unsafe.getUnsafe();
private static final long valueOffset;

static {
    try {
        // 获取“value”字段在AtomicInteger类中的内存偏移量
        valueOffset = unsafe.objectFieldOffset
            (AtomicInteger.class.getDeclaredField("value"));
    } catch (Exception ex) { throw new Error(ex); }
}
// 确保“value”字段的可见性
private volatile int value;

// 如果当前值等于预期值，则原子地将值设置为newValue
// 使用 Unsafe#compareAndSwapInt 方法进行CAS操作
public final boolean compareAndSet(int expect, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}

// 原子地将当前值加 delta 并返回旧值
public final int getAndAdd(int delta) {
    return unsafe.getAndAddInt(this, valueOffset, delta);
}

// 原子地将当前值加 1 并返回加之前的值（旧值）
// 使用 Unsafe#getAndAddInt 方法进行CAS操作。
public final int getAndIncrement() {
    return unsafe.getAndAddInt(this, valueOffset, 1);
}

// 原子地将当前值减 1 并返回减之前的值（旧值）
public final int getAndDecrement() {
    return unsafe.getAndAddInt(this, valueOffset, -1);
}
```


Unsafe#getAndAddInt源码：
```java
// 原子地获取并增加整数值
public final int getAndAddInt(Object o, long offset, int delta) {
    int v;
    do {
        // 以 volatile 方式获取对象 o 在内存偏移量 offset 处的整数值
        v = getIntVolatile(o, offset);
    } while (!compareAndSwapInt(o, offset, v, v + delta));
    // 返回旧值
    return v;
}
```

## ⭐️synchronized 和 volatile 有什么区别？
synchronized 关键字和 volatile 关键字是两个互补的存在，而不是对立的存在！
* volatile 关键字是线程同步的轻量级实现，所以 volatile性能肯定比synchronized关键字要好 。但是 volatile 关键字只能用于变量而 synchronized 关键字可以修饰方法以及代码块 。
* volatile 关键字能保证数据的可见性，但不能保证数据的原子性。synchronized 关键字两者都能保证。
* volatile关键字主要用于解决变量在多个线程之间的可见性，而 synchronized 关键字解决的是多个线程之间访问资源的同步性。


## 锁 
https://javaguide.cn/java/concurrent/java-concurrent-questions-02.html#%E2%AD%90%EF%B8%8Fsynchronized-%E5%92%8C-reentrantlock-%E6%9C%89%E4%BB%80%E4%B9%88%E5%8C%BA%E5%88%AB