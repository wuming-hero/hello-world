package com.wuming.util;

import com.wuming.model.Person;
import com.wuming.model.Student;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 类转换工具类
 *
 * @author manji
 * Created on 2024/11/9 08:58
 */
public class BeanConvertUtil {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BeanConvertUtil.class);

    public static <S, T> T to(S src, Class<T> targetCls) {
        return to(src, targetCls, null);
    }

    public static <S, T> T to(S src, Class<T> targetCls, BiConsumer<S, T> proceesor) {
        if (src == null) return null;
        try {
            T t = targetCls.newInstance();
            // org.apache.commons.beanutils.BeanUtils.copyProperties 第一个参数是目标对象，第二个参数是源对象
            BeanUtils.copyProperties(t, src);

            if (proceesor != null) proceesor.accept(src, t);

            return t;
        } catch (Exception e) {
            log.error("convert failed", e);
        }
        return null;
    }

    public static <S, T> T toFirst(List<S> srcList, Class<T> targetCls) {
        if (srcList.size() == 0) return null;
        return to(srcList.get(0), targetCls);
    }

    public static <S, T> T toFirst(List<S> srcList, Class<T> targetCls, BiConsumer<S, T> processor) {
        if (srcList.size() == 0) return null;
        return to(srcList.get(0), targetCls, processor);
    }

    public static <S, T> List<T> to(List<S> srcList, Class<T> targetCls) {
        if (CollectionUtils.isEmpty(srcList)) return Collections.emptyList();
        return srcList.parallelStream().map(o -> to(o, targetCls)).collect(Collectors.toList());
    }

    public static <S, T> List<T> to(List<S> srcList, Class<T> targetCls, BiConsumer<S, T> processor) {
        if (CollectionUtils.isEmpty(srcList)) return Collections.emptyList();
        return srcList.parallelStream().map(o -> to(o, targetCls, processor)).collect(Collectors.toList());
    }

    /**
     * Person 转换为Student定制逻辑
     *
     * @param person
     * @param student
     */
    private static void person2Student(Person person, Student student) {
        if (Objects.isNull(student)) {
            return;
        }
        student.setHasMore(Objects.nonNull(person.getArea()));
    }


    public static void main(String[] args) {
        Person person = new Person();
        person.setName("wuming");
        person.setArea("beijing");
        Student student = BeanConvertUtil.to(person, Student.class, BeanConvertUtil::person2Student);
        System.out.println(student);
    }

}
