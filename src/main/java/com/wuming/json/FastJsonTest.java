package com.wuming.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.wuming.model.Account;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        String accountStr = JSON.toJSONString(json);
        String accountListStr = JSON.toJSONString(list);
        System.out.println(accountStr);
        System.out.println();

        // Json字符串反序列化成对象
        Account person = JSON.parseObject(accountStr, Account.class);
        System.out.printf("name: %s, id: %d\n", person.getName(), person.getId());

        //数组对象反序列化成集合
        List<Account> listPerson = JSON.parseArray(accountListStr, Account.class);
        for (Account item : listPerson) {
            System.out.println("id: " + item.getId() + "name:" + item.getName());
        }
        // 3. 复杂类型 反序列化，可以通过TypeReference实现
        Map<String, Account> accountMap = Maps.newHashMap();
        accountMap.put("accountKey", json);
        String mapStr = JSON.toJSONString(accountMap);
        System.out.println(mapStr);
        // 3.1简单类型直接转换也是可以的，accountMap2 的value 是JSONObject类型，此处涉及到类型强制转换，所以会有强转警告
        Map<String, Account> accountMap2 = JSON.parseObject(mapStr, Map.class);
        System.out.println(accountMap2);

        // 3.2 通过TypeReference 方式转换，更安全优雅
        Map<String, Account> accountMap3 = JSON.parseObject(mapStr, new TypeReference<Map<String, Account>>() {});
        System.out.println(accountMap3);
    }

    /**
     * 测试方法，用于演示如何解析JSON字符串并提取数据。
     *
     * <p>该方法将一个JSON格式的字符串解析为JSONObject，然后从中获取订单ID并打印其大整数和长整型值。</p>
     *
     * @param 无参数
     * @return 无返回值
     */
    @Test
    public void test2() {
        String a = "{\"gmt_create\":\"2022-11-24 21:25:42\",\"order_id\":\"8519902714490971\"}";
        JSONObject jsonObject = JSON.parseObject(a);
        System.out.println(jsonObject.getBigInteger("order_id"));
        System.out.println(jsonObject.getLong("order_id"));

    }

    /**
     * json转Map 特殊场景测试
     */
    @Test
    public void jsonToMapSpecialTest() {
        // int的最大值 2147483647
        System.out.println(Integer.MAX_VALUE);
        // asrId 特意设置为long值
        String extendedMapJson = "{\"asrId\":2147483648,\"asrType\":\"2\",\"entrySource\":\"scan_station_code_promotion\",\"terminalSource\":1001}";
        /*
         通过fastjson将字符串转换为Map对象后，里面的数据值类型的数据转换后并非期望的Sting类型，而是会转为对应数值类型
         比如
         asrType 在Map对象中的类型为String
         aseId 在Map对象中的类型为Long
         terminalSource 在Map对象中的类型为Integer
         */

        Map<String, String> dataMap = JSON.parseObject(extendedMapJson, Map.class);
        System.out.println(dataMap);

        // TODO manji 2023/3/20 16:49 以下方式获取数值类型的数据将会报类型转换错误，java.lang.Integer、java.lang.Long xx can't cast to java.lang.String
//        String terminalSource = dataMap.get("terminalSource");
//        String asrId = dataMap.get("asrId");

        // 正确的数据获取方式,然后判空后再转为对应
        Object terminalSourceObj = dataMap.get("terminalSource");
        Object asrIdObj = dataMap.get("asrId");
        Integer terminalSource = null;
        Long asrId = null;

        /*****************方法一 *********************/
        terminalSource = Integer.valueOf(terminalSourceObj.toString());
        asrId = Long.valueOf(asrIdObj.toString());

        /*******************方法二*********************/
        /**
         * 这里虽然idea语法检测上会warn，但是可以运行的
         */
        if (terminalSourceObj instanceof Integer) {
            terminalSource = (Integer) terminalSourceObj;
        }
        if (asrIdObj instanceof Long) {
            asrId = (Long) asrIdObj;
        }
        System.out.println(terminalSource);
        System.out.println(asrId);
    }
}
