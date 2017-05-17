package com.wuming.model;

import java.io.Serializable;

/**
 * Created by wuming on 2017/5/8.
 */
public class Test extends Account implements Serializable {
    private static final long serialVersionUID = -1718380602876191306L;

    private Integer age;

    public static void main(String[] args) {
        Test test = new Test();
        System.out.println(test.getEmail());
        System.out.println(test);
    }

    public Integer getAge() {
        return age;
    }

//    @Override
//    public String toString() {
//        return "Test{" +
//                "age=" + age +
//                '}';
//    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
