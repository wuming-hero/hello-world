# ThreadLocal原理

## ThreadLocal简介:
通常情况下，我们创建的变量是可以被任何一个线程访问并修改的。如果想实现每一个线程都有自己的 专属本地变 量该如何解决呢? 
JDK中提供的 ThreadLocal 类正是为了解决这样的问题。类似操作系统中的TLAB

## 原理:
首先 ThreadLocal 是一个泛型类，保证可以接受任何类型的对象。
因为一个线程内可以存在多个 ThreadLocal 对象，所以其实是 ThreadLocal 内部维护了一个 Map ，是 ThreadLocal 实现的一个叫做 ThreadLocalMap 的静态内部类。
最终的变量是放在了当前线程的 ThreadLocalMap 中，并不是存在 ThreadLocal上，ThreadLocal可以理解为只是ThreadLocalMap的封装，传递了变量值。
我们使用的 get()、set() 方法其实都是调用了这个ThreadLocalMap类对应的 get()、set() 方法。

```java
// ThreadLocalMap设计
static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value; // 存储 ThreadLocal 对应的变量值
    Entry(ThreadLocal<?> k, Object v) {
        super(k); // 键是 ThreadLocal 的弱引用
        value = v;
    }
}

// ThreadLocal 类使用
public class ThreadLocal<T> {
    
    private T get(Thread t) {
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T) e.value;
                return result;
            }
        }
        return setInitialValue(t);
    }
}
```

## ThreadLocal内存泄漏的场景
实际上 ThreadLocalMap 中使用的 key 为 ThreadLocal 的弱引用，而 value 是强引用。
弱引用的特点是，如果这个对象持有弱引用，那么在下一次垃圾回收的时候必然会被清理掉。

所以如果 ThreadLocal 没有被外部强引用的情况下，在垃圾回收的时候会被清理掉的，这样一来 ThreadLocalMap中使用这个 ThreadLocal 的 key 也会被清理掉。
但是，value 是强引用，不会被清理，这样一来就会出现key为 null 的value。
假如我们不做任何措施的话，value 永远无法被GC 回收，如果线程长时间不被销毁，可能会产生内存泄露。

ThreadLocalMap实现中已经考虑了这种情况，在调用 set()、get()、remove() 方法的时候，会清理掉 key 为 null 的记录。
如果说会出现内存泄漏，那只有在出现了 key 为 null 的记录后，没有手动调用 remove() 方法，并且之后也不再调用 get()、set()、remove()方法的情况下。
因此使用完ThreadLocal方法后，最好手动调用 remove () 方法。

### 为什么键必须是弱引用？
> 假设 ThreadLocalMap的键是强引用，会导致内存泄漏风险

当一个 ThreadLocal变量不再被外部使用（即外部代码中已没有对该 ThreadLocal实例的强引用）时，理论上应该被垃圾回收（GC）。
但由于 ThreadLocalMap的键是强引用，ThreadLocal实例会被 ThreadLocalMap强引用， 导致：

* 即使外部没有其他引用，ThreadLocal实例仍被 ThreadLocalMap持有，无法被 GC。
* 同时，ThreadLocalMap中对应的 value会被该 entry 强引用（因为 value是普通对象），导致 value也无法被 GC。

最终，ThreadLocal实例和对应的 value会永久驻留在内存中，形成内存泄漏（尤其是在线程长期存活的场景下，如线程池）。

### 弱引用的作用：释放无效的 ThreadLocal 实例
将键改为 WeakReference<ThreadLocal<?>>后，ThreadLocal实例的生命周期不再被 ThreadLocalMap强制绑定：

* 当外部对 ThreadLocal实例的强引用全部消失时（即没有代码再引用该实例），即使 ThreadLocalMap中仍有该实例的弱引用，GC 也会回收该 ThreadLocal实例（因为弱引用不会阻止 GC）。
* 此时，ThreadLocalMap中对应的 entry 的键变为 null（弱引用指向的对象被回收后，弱引用自身会被标记为 null），但 value仍被 entry 强引用。

### 弱引用的设计权衡
ThreadLocalMap使用弱引用作为键的核心目的是：

* 避免 ThreadLocal实例的内存泄漏：当外部不再需要 ThreadLocal时，允许 GC 回收该实例，即使线程仍在运行。
* 配合清理机制降低 value泄漏风险：通过弱引用标记无效 entry，结合 get()、set()、remove()等操作的主动清理，尽可能释放 value占用的内存。
