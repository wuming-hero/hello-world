package com.wuming.util;

import com.google.common.collect.ImmutableMap;
import com.wuming.model.Account;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wuming
 * Created on 2017/9/11 20:04
 */
public class BeanRefUtil {

    public static void main(String[] args) {
        Account account = new Account();
        Map<String, String> map = ImmutableMap.of("id", "1", "name", "wuming");
        setFieldValue(account, map);
        System.out.println("account: " + account);
        System.out.println("map value: " + getFieldValueMap(account));
    }

    /**
     * 取Bean的属性和值对应关系的MAP
     *
     * @param bean
     * @return Map
     */
    public static Map<String, String> getFieldValueMap(Object bean) {
        Class<?> clazz = bean.getClass();
        Map<String, String> valueMap = new HashMap<>();
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldType = field.getType().getSimpleName();
                String fieldGetName = parseGetMethod(field.getName());
                if (!checkMethod(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMethod = clazz.getMethod(fieldGetName);
                Object fieldVal = fieldGetMethod.invoke(bean);
                String result = null;
                if (Objects.nonNull(fieldVal)) {
                    if ("Date".equals(fieldType)) {
                        result = DateUtil.dateTimeToString((Date) fieldVal);
                    } else {
                        result = String.valueOf(fieldVal);
                    }
                }
                valueMap.put(field.getName(), result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return valueMap;
    }

    /**
     * set属性的值到Bean
     *
     * @param bean
     * @param valMap
     */
    public static void setFieldValue(Object bean, Map<String, String> valMap) {
        Class<?> clazz = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                String fieldSetName = parseSetMethod(field.getName());
                if (!checkMethod(methods, fieldSetName)) {
                    continue;
                }
                Method fieldSetMethod = clazz.getMethod(fieldSetName, field.getType());
                String fieldKeyName = field.getName();
                String value = valMap.get(fieldKeyName);
                if (StringUtils.isNotBlank(value)) {
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equals(fieldType)) {
                        fieldSetMethod.invoke(bean, value);
                    } else if ("Date".equals(fieldType)) {
                        fieldSetMethod.invoke(bean, DateUtil.dateTimeStringToDate(value));
                    } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                        fieldSetMethod.invoke(bean, Integer.parseInt(value));
                    } else if ("Long".equalsIgnoreCase(fieldType)) {
                        fieldSetMethod.invoke(bean, Long.parseLong(value));
                    } else if ("Double".equalsIgnoreCase(fieldType)) {
                        fieldSetMethod.invoke(bean, Double.parseDouble(value));
                    } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                        fieldSetMethod.invoke(bean, Boolean.parseBoolean(value));
                    } else {
                        System.out.println("not supper type" + fieldType);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测是不有指定方法
     *
     * @param methods
     * @param fieldMethod
     * @return
     */
    public static boolean checkMethod(Method[] methods, String fieldMethod) {
        for (Method method : methods) {
            if (fieldMethod.equals(method.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parseGetMethod(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "get" + String.valueOf(fieldName.charAt(startIndex)).toUpperCase() + fieldName.substring(startIndex + 1);
    }

    /**
     * 拼接属性的 set方法
     *
     * @param fieldName
     * @return String setXxx
     */
    public static String parseSetMethod(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set" + String.valueOf(fieldName.charAt(startIndex)).toUpperCase() + fieldName.substring(startIndex + 1);
    }

}
