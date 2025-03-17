package com.wuming.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 登记模式
 * @author wuming
 * Created on 2020-11-18 07:39
 */
public class Singleton5 {


    private static Map<String, Singleton5> singleton3Map = new HashMap<>();
    static {
        Singleton5 singleton3  = new Singleton5();
        singleton3Map.put(singleton3.getClass().getName(), singleton3);
    }

    public static Singleton5 getSingleton3() {
        return singleton3Map.get(Singleton5.class.getName());
    }

    public static Singleton5 getSingleton3(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (Objects.isNull(name)) {
            name = Singleton5.class.getName();
        }
        if (Objects.isNull(singleton3Map.get(name))) {
            singleton3Map.put(name, (Singleton5) Class.forName(name).newInstance());
        }
        return singleton3Map.get(name);
    }

}
