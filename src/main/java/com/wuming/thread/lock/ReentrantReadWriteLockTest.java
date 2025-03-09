package com.wuming.thread.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用synchronized可以实现同步，但是缺点是同时只有一个线程可以访问共享变量，
 * 但是正常情况下，对于多个读操作操作共享变量时候是不需要同步的，synchronized时候无法实现多个读线程同时执行，而大部分情况下读操作次数多于写操作，所以这大大降低了并发性，
 * 所以出现了ReentrantReadWriteLock，它可以实现读写分离，运行多个线程同时进行读取，但是最多运行一个写现线程存在。
 * <p>
 * 假如一个线程已经获取了读锁，这时候如果一个线程要获取写锁时候要等待直到释放了读锁，
 * 如果一个线程获取了写锁，那么所有获取读锁的线程需要等待直到写锁被释放。
 * <p>
 * 所以相比synchronized来说运行多个读者同时存在，所以提高了并发量。
 * 注意 需要使用者显示调用Lock与unlock操作
 *
 * @author manji
 * Created on 2025/3/7 10:15
 */
public class ReentrantReadWriteLockTest<E> {

    // 模拟数据集合
    private final List<E> list = new ArrayList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public E get(int index) {

        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            return list.get(index);
        } finally {
            readLock.unlock();
        }
    }

    public E set(int index, E element) {

        Lock wirteLock = readWriteLock.writeLock();
        wirteLock.lock();
        try {
            return list.set(index, element);
        } finally {
            wirteLock.unlock();
        }
    }

}
