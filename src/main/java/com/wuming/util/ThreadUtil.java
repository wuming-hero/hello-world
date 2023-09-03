package com.wuming.util;

import com.google.common.collect.Lists;
import com.wuming.model.Student;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author manji
 * Created on 2023/9/3 13:04
 */
public class ThreadUtil {


    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);


    /**
     * 异步执行单个任务，返回执行结果
     *
     * @param callable Callable任务
     * @param executorService 线束池
     * @param timeoutMs 超时时间，单位：ms
     * @return 异步任务执行结果
     * @param <V>
     */
    public static  <V> V runSingle(Callable<V> callable, ExecutorService executorService, Long timeoutMs) {
        if (callable == null) {
            return null;
        }
        final Future<V> future = executorService.submit(callable);
        try {
            V v = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            if (v != null) {
                return future.get();
            }
        } catch (Throwable e) {
            logger.error("error to execute.", e);
        }
        return null;
    }

    /**
     * 异步执行批量任务，返回结果列表
     *
     * @param callableList Callable任务列表
     * @param executorService 线程池
     * @param timeoutMs 每个任务超时时间，单位：ms
     * @return
     * @param <V>
     */
    public static  <V> List<V> run(List<Callable<V>> callableList, ExecutorService executorService, Long timeoutMs) {
        if (CollectionUtils.isEmpty(callableList)) {
            return Collections.emptyList();
        }
        List<Future<V>> futures = new ArrayList<>(callableList.size());
        for (Callable<V> callable : callableList) {
            futures.add(executorService.submit(callable));
        }
        List<V> result = new ArrayList<>();
        for (Future<V> future : futures) {
            try {
                V v = future.get(timeoutMs, TimeUnit.MILLISECONDS);
                if (v != null) {
                    result.add(v);
                }
            } catch (Throwable e) {
                logger.error("error to execute.", e);
            }
        }
        return result;
    }

    /**
     * 批量异步执行任务，不返回结果
     *
     * @param callableList Callable任务列表
     * @param executorService 线程池
     * @param <V>
     */
    public static  <V> void runAsync(List<Callable<V>> callableList, ExecutorService executorService) {
        if (CollectionUtils.isEmpty(callableList)) {
            return;
        }
        for (Callable<V> callable : callableList) {
            executorService.submit(callable);
        }
    }

}
