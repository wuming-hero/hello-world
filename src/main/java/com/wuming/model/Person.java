package com.wuming.model;

import java.io.Serializable;

/**
 * @author wuming
 * Created on 2017/11/15 11:55
 */
public class Person implements Serializable {

    private static final long serialVersionUID = -8417199738140859034L;

    private String name;
    private int salary;
    private int age;
    private String sex;
    private String area;

    public Person() {
    }

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Person(String name, int salary, int age, String sex, String area) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.sex = sex;
        this.area = area;
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

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", sex='" + sex + '\'' +
                ", area='" + area + '\'' +
                '}';
    }

}