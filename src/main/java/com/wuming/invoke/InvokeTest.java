package com.wuming.invoke;

import com.wuming.model.Account;
import com.wuming.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
    private static Field getField(Object object, String name) {
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
     * 获得类的get与set方法，
     * 然后通过invoke()方法获得类成员变量值
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
     * 调用 Field 的 set(), get() 方法，修改和获取类成员变量
     * 需要修改字段的访问权限
     */
    @Test
    public void fieldTest() {
        String key = "name";
        Field field = getField(account, key);
        if (Objects.nonNull(field)) {
            // 修改字段的访问权限
            field.setAccessible(Boolean.TRUE);
            try {
                // 赋值
                field.set(account, "张三");
                // 获取值
                System.out.println(field.getName() + "=" + field.get(account));
                // 正常获得值
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
        Field field = getField(account, key);
        Glob t = field.getAnnotation(Glob.class);
        String val = t.key();
        System.out.println(key + ": " + val);
    }

    @Test
    public void extendsTest() {
        Student student = new Student();
        // 父类属性
        student.setId(1);
        student.setEmail("123@qq.com");
        student.setAddress("杭州");
        // 扩展属性
        student.setAge(27);
        // 重写属性
        student.setName("无名");
        Field[] fields = student.getClass().getDeclaredFields();
        System.out.println("fieldList: " + Arrays.asList(fields));
        // 继承父类的属性
        String key = "id";
        try {
            // 通过clazz.getSuperclass()获得Field
            Field field = student.getClass().getSuperclass().getDeclaredField(key);
            field.setAccessible(true);
            System.out.println("key: " + key + ", value: " + field.get(student));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过 PropertyDescriptor 获得类的方法
     * 进而通过反射为类的成员变量赋值和取值
     * 注：包括从父类继承的属性
     */
    @Test
    public void extendsTest2() {
        Student student = new Student();
        // 父类属性
        student.setId(1);
        student.setEmail("123@qq.com");
        student.setAddress("杭州");
        // 扩展属性
        student.setAge(27);
        // 重写属性
        student.setName("无名");
        Field[] fields = student.getClass().getDeclaredFields();
        System.out.println("fieldList: " + Arrays.asList(fields));
        // 继承父类的属性
        String key = "id";
        try {
            Class<?> clazz = student.getClass();
            //使用符合JavaBean规范的属性访问器
            PropertyDescriptor pd = new PropertyDescriptor(key, clazz);
            //调用setter
            Method writeMethod = pd.getWriteMethod();    //setName()
            writeMethod.invoke(student, 2);

            //调用getter
            Method readMethod = pd.getReadMethod();        //getName()
            Object value = readMethod.invoke(student);
            System.out.println("value: " + value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果当前类中没有指定属性，
     * 循环去父类中查询，找到基类为止
     */
    @Test
    public void extendsTest3() {
        String key = "id";
        try {
            Class<?> clazz = Class.forName("com.wuming.model.Student");
            Object student = clazz.newInstance();
            Field personNameField = null;
            // 循环去父类中上查找 field 字段
            for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    personNameField = clazz.getDeclaredField(key);
                    break;
                } catch (Exception e) {
                    System.out.println(clazz.getName() + " 中没有 " + key + " 属性！");
                }
            }
            personNameField.setAccessible(true);
            personNameField.set(student, 12);
            System.out.println(key + ":" + personNameField.get(student));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
