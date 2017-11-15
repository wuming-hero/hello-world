package com.wuming.model;

import java.io.Serializable;

/**
 * @author wuming
 * Created on 2017/11/15 11:55
 */
public class Person implements Serializable {

    private static final long serialVersionUID = -8417199738140859034L;

    private int age;
    private String name;

    public Person() {
    }

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name + "-->" + this.age;
    }

}