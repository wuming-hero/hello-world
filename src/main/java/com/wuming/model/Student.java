package com.wuming.model;

import java.io.Serializable;

/**
 * Created by wuming on 2017/5/8.
 */
public class Student extends Account implements Serializable {
    private static final long serialVersionUID = -1718380602876191306L;

    private Integer age;
    private String name;

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
}
