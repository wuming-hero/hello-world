JMM 是一种规范，是解决由于多线程通过共享内存进行通信时，存在的本地内存数据不一致、编译器会对代码指 令重排序、处理器会对代码乱序执行等带来的问题。目的是保证并发编程场景中的原子性、可见性和有序性。

## 线程安全
### 原子性:
在 Java 中，为了保证原子性，提供了两个高级的字节码指令 Monitorenter 和 Monitorexit。这两个字节码，在 Java 中对应的关键字就是 Synchronized。因此，在 Java 中可以使用 Synchronized 来保证方法和代码块内的 操作是原子性的。
### 可见性:
Java 中的 Volatile 关键字修饰的变量在被修改后可以立即同步到主内存。被其修饰的变量在每次使用之前都从主 内存刷新。因此，可以使用 Volatile 来保证多线程操作时变量的可见性。除了 Volatile，Java 中的 Synchro- nized 和 Final 两个关键字也可以实现可见性。只不过实现方式不同
### 有序性
在 Java 中，可以使用 Synchronized 和 Volatile 来保证多线程之间操作的有序性。区别:Volatile 禁止指令重 排。Synchronized 保证同一时刻只允许一条线程操作。


## volatile 关键字

### 作用
保证数据的“可见性”:被volatile修饰的变量能够保证每个线程能够获取该变量的最新值，从而避免出现数据脏读 的现象。
禁止指令重排:在多线程操作情况下，指令重排会导致计算结果不一致


### 底层实现:
“观察加入volatile关键字和没有加入volatile关键字时所生成的汇编代码发现，加入volatile关键字时，会多出一个 lock前缀指令”，
lock前缀指令实际上相当于一个内存屏障(也称内存栅栏)，内存屏障会提供3个功能:
1. 它确保指令重排序时不会把其后面的指令排到内存屏障之前的位置，也不会把前面的指令排到内存屏障的后面; 
2. 它会强制将对缓存的修改操作立即写入主存;
3. 如果是写操作，它会导致其他CPU中对应的缓存行无效。

### 单例模式中volatile的作用:
防止代码读取到instance不为null时，instance引用的对象有可能还没有完成初始化。

```java
class Singleton{
    private volatile static Singleton instance = null; 
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        if(instance==null) { //减少加锁的损耗 
            synchronized (Singleton.class) {
                if(instance==null) {//确认是否初始化完成 
                    instance = new Singleton();
                }
            } 
        }
        return instance; 
    }
}
```