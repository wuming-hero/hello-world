package com.wuming.model;

import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wuming on 2017/5/8.
 */
public class Student extends Account implements Serializable {
    private static final long serialVersionUID = -1718380602876191306L;

    private Integer age;
    private String name;
    private Map<String, String> featureMap;

    public static void main(String[] args) {
        Student test = new Student();
        System.out.println(test.getEmail());
        System.out.println(test);
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    /**
     * 增加feature属性
     *
     * @param key
     * @param value
     */
    public void addFeature(String key, String value) {
        if (Objects.isNull(featureMap)) {
            featureMap = new HashMap<>();
        }
        featureMap.put(key, value);
    }

    /**
     * 获取feature属性的值
     *
     * @param key
     * @return
     */
    public String getFeature(String key) {
        if (MapUtils.isEmpty(featureMap)) {
            return null;
        }
        return featureMap.get(key);
    }

}
