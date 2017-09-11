package com.wuming.guava;

import com.google.common.cache.*;
import com.wuming.model.Account;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author wuming
 * Created on 2017/8/14 10:11
 */
public class CacheTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
        LoadingCache<Integer, Account> accountCache
                // CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
                = CacheBuilder.newBuilder()
                // 设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(8)
                // 设置写缓存后8秒钟过期
                .expireAfterWrite(8, TimeUnit.SECONDS)
                // 设置缓存容器的初始容量为2
                .initialCapacity(2)
                // 设置缓存最大容量为2，超过2之后就会按照LRU最近虽少使用算法来移除缓存项
                .maximumSize(2)
                // 设置要统计缓存的命中率
                .recordStats()
                // 设置缓存的移除通知
                .removalListener(new RemovalListener<Object, Object>() {
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
                    }
                })
                // build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(new CacheLoader<Integer, Account>() {
                    @Override
                    public Account load(Integer id) throws Exception {
                        System.out.println("load student " + id);
                        Account student = new Account();
                        student.setId(id);
                        student.setName("name " + id);
                        return student;
                    }
                });

        // 最后打印缓存的命中率等 情况
        System.out.println("cache stats:" + accountCache.stats());

        // 获得 id=1的student，只有第1次会load student, 2、3次直接从缓存获取
        Account account = accountCache.get(1);
        System.out.println(account);

        account = accountCache.get(1);
        System.out.println(account);

        account = accountCache.get(1);
        System.out.println(account);

        account = accountCache.get(2);
        System.out.println(account);

        // 初始容量为2，缓存第3个的时候，第一个会被移除
        account = accountCache.get(3);
        System.out.println(account);
    }
}
