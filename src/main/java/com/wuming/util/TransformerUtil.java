package com.wuming.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于函数式编程，对列表对象进行转换
 *
 * @author manji
 * Created on 2025/3/1 11:43
 */
public class TransformerUtil {

    /**
     * 将一个对象列表按照指定转换函数转换为另一个对象的列表
     *
     * @param sourceCollection
     * @param function
     * @return
     */
    public static <S, R> List<R> transformList(Collection<S> sourceCollection, Function<S, R> function) {
        List<R> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return resultList;
        }
        for (S source : sourceCollection) {
            resultList.add(function.apply(source));
        }
        return resultList;
    }

    /**
     * 将一个对象集合按照指定转换函数转换为另一个对象的集合
     *
     * @param sourceCollection
     * @param function
     * @return
     */
    public static <S, R> Set<R> transformSet(Collection<S> sourceCollection, Function<S, R> function) {
        Set<R> resultSet = Sets.newHashSet();
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return resultSet;
        }
        for (S source : sourceCollection) {
            resultSet.add(function.apply(source));
        }
        return resultSet;
    }

    /**
     * 将List按照指定的keyMapper转换为相应的Map
     *
     * @param list
     * @param keyMapper 将list的元素对象转换为Map的key
     * @return
     */
    public static <T, K> Map<K, T> transformListToMap(List<T> list, Function<T, K> keyMapper) {
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        Map<K, T> map = Maps.newHashMap();
        for (T t : list) {
            map.put(keyMapper.apply(t), t);
        }
        return map;
    }

    /**
     * 将List按照指定的keyMapper和valueMapper转换为相应的Map
     *
     * @param list
     * @param keyMapper   将list的元素对象转换为Map的key
     * @param valueMapper 将list的元素对象转换为Map的value
     * @return
     */
    public static <T, K, V> Map<K, V> transformListToMap(List<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

}
