package com.wuming.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuming.model.Account;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuming
 * Created on 2017/8/2 18:38
 */
public class FastJsonTest {

    @Test
    public void test() {
        Account json = new Account(19, "李明");
        List<Account> list = new ArrayList<>();
        list.add(json);
        list.add(new Account(12, "张三"));

        // 将集合或者对象序例化成JSON
        System.out.println(JSON.toJSON(json));
        System.out.println(JSON.toJSON(list));

        // Json字符串反序列化成对象
        Account person = JSON.parseObject("{\"name\":\"李明\",\"age\":19}", Account.class);
        System.out.printf("name: %s, id: %d\n", person.getName(), person.getId());

        String str = "[{\"name\":\"李明\",\"age\":19},{\"name\":\"张三\",\"age\":12}]";
        //数组对象反序列化成集合
        List<Account> listPerson = JSON.parseArray(str, Account.class);
        for (Account item : listPerson) {
            System.out.println("id: " + item.getId() + "name:" + item.getName());
        }

        //字符串转换为JSON对象
        JSONObject jsonObject = JSON.parseObject("{\"name\":\"李明\",\"age\":19}");
        System.out.printf("name:%s, age:%d\n", jsonObject.getString("name"), jsonObject.getBigInteger("age"));

        //字符串转换为JSON数组
        JSONArray jsonArray = JSON.parseArray("[{\"name\":\"李明\",\"age\":19},{\"name\":\"张三\",\"age\":12}]");
        for (int i = 0, len = jsonArray.size(); i < len; i++) {
            JSONObject temp = jsonArray.getJSONObject(i);
            System.out.printf("name: %s, age: %d\n", temp.getString("name"), temp.getBigInteger("age"));
        }

        for (Object obj : jsonArray) {
            System.out.println(obj.toString());
        }
    }
}
