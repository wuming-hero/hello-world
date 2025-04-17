# ThreadLocal原理

## ThreadLocal简介:
通常情况下，我们创建的变量是可以被任何一个线程访问并修改的。如果想实现每一个线程都有自己的 专属本地变 量该如何解决呢? JDK中提供的 ThreadLocal 类正是为了解决这样的问题。类似操作系统中的TLAB

## 原理:
首先 ThreadLocal 是一个泛型类，保证可以接受任何类型的对象。因为一个线程内可以存在多个 ThreadLocal 对 象，所以其实是 ThreadLocal 内部维护了一个 Map ，是 ThreadLocal 实现的一个叫做 ThreadLocalMap 的静 态内部类。
最终的变量是放在了当前线程的 ThreadLocalMap 中，并不是存在 ThreadLocal 上，ThreadLocal 可以理解为 只是ThreadLocalMap的封装，传递了变量值。
我们使用的 get()、set() 方法其实都是调用了这个ThreadLocalMap类对应的 get()、set() 方法。

## ThreadLocal内存泄漏的场景
实际上 ThreadLocalMap 中使用的 key 为 ThreadLocal 的弱引用，而 value 是强引用。
弱引用的特点是，如果 这个对象持有弱引用，那么在下一次垃圾回收的时候必然会被清理掉。

所以如果 ThreadLocal 没有被外部强引用的情况下，在垃圾回收的时候会被清理掉的，这样一来 ThreadLocalMap中使用这个 ThreadLocal 的 key 也会被清理掉。
但是，value 是强引用，不会被清理，这样一来就会出现 key 为 null 的 value。
假如我们不做任何措施的话，value 永远无法被GC 回收，如果线程长时间不被销毁，可能 会产生内存泄露。

ThreadLocalMap实现中已经考虑了这种情况，在调用 set()、get()、remove() 方法的时候，会清理掉 key 为 null 的记录。
如果说会出现内存泄漏，那只有在出现了 key 为 null 的记录后，没有手动调用 remove() 方法，并且之后也不再调用 get()、set()、remove()方法的情况下。
因此使用完ThreadLocal方法后，最好手动调用 remove () 方法。