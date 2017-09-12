package com.wuming.model;

import lombok.Data;

/**
 * @author wuming
 * Created on 2017/9/11 20:48
 */
@Data
public class Parent {

    public String publicField = "1";
    protected String protectedField = "3";
    String defaultField = "2";
    private String privateField = "4";

    public void publicMethod() {
        System.out.println("publicMethod...");
    }

    void defaultMethod() {
        System.out.println("defaultMethod...");
    }

    protected void protectedMethod() {
        System.out.println("protectedMethod...");
    }

    private void privateMethod() {
        System.out.println("privateMethod...");
    }
}
