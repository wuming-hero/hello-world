package com.wuming.model;

import com.wuming.invoke.Glob;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by wuming on 2017/4/15.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 719558489329330505L;

    /**
     * 特殊注解，使用反映获取key的值
     */
    @Glob(key = "stand.id")
    private Integer id;
    private String name;
    private String email;
    private String address;

    public Account() {
    }

    public Account(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    @Override
//    public String toString() {
//        return "Account{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", address='" + address + '\'' +
//                '}';
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append(":").append(field.get(this)).append(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 去掉多于的逗号
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }
}
