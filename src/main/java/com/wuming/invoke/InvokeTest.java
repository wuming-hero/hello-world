package com.wuming.invoke;

import com.wuming.model.Account;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author wuming
 * Created on 2017/8/1 21:02
 */
public class InvokeTest {

    private Account account;

    /**
     * 查找某个类是否有该属性
     *
     * @param object
     * @param name
     * @return
     */
    private static Field searchField(Object object, String name) {
        Field[] fields = object.getClass().getDeclaredFields();
        String internedName = name.intern();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName() == internedName) {
                return fields[i];
            }
        }
        return null;
    }

    @Before
    public void init() {
        account = new Account();
        account.setId(1);
        account.setName("无名");
    }

    /**
     * 通过invoke()方法获得类成员变量值
     */
    @Test
    public void test() {
        String key = "name";
        System.out.println(key + "----" + key.intern());
        // 构建 getName() 方法
        String getMethod = "get" + String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1);
        System.out.println("getMethod = " + getMethod);
        try {
            // 获得类的getXxx() 方法, 会抛 NoSuchMethodException 异常
            Method method = account.getClass().getDeclaredMethod(getMethod);
            // 通过invoke()方法获得值
            Object value = method.invoke(account);
            System.out.println("value = " + value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 循环类中所有的字段，修改字段的访问权限，然后获得其值
     */
    @Test
    public void test2() {
        String key = "name";
        Field field = searchField(account, key);
        if (Objects.nonNull(field)) {
            // 修改字段的访问权限
            field.setAccessible(Boolean.TRUE);
            try {
                System.out.println(field.getName() + "=" + field.get(account));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过反射修改类的成员变量
     */
    @Test
    public void test3() {
        String key = "name";
        Field field = searchField(account, key);
        if (Objects.nonNull(field)) {
            try {
                field.setAccessible(Boolean.TRUE); // 取消访问检查
                field.set(account, "张三");
                System.out.println(key + ": " + account.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取某个类的注解字段。
     */
    @Test
    public void getKey() {
        String key = "id";
        Field[] field = account.getClass().getDeclaredFields();

        for (Field f : field) {
            if (f.getName().equals(key)) {
                Glob t = f.getAnnotation(Glob.class);
                String val = t.key();
                System.out.println(key + ": " + val);
                break;
            }
        }

    }

}
