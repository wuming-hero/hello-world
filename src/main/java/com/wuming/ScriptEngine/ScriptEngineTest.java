package com.wuming.ScriptEngine;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by wuming on 2017/4/11.
 * <p>
 * Nashorn，发音“nass-horn”,是德国二战时一个坦克的命名，同时也是java8新一代的javascript引擎，
 * 替代老旧、缓慢的Rhino，符合 ECMAScript-262 5.1 版语言规范。
 * 你可能想javascript是运行在web浏览器，提供对html各种dom操作，但是Nashorn不支持浏览器DOM的对象。
 */
public class ScriptEngineTest {

    @Test
    public void test() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
        String name = "Mahesh";
        Integer result = null;
        try {
            engine.eval("print('" + name + "')");
            result = (Integer) engine.eval("10 + 2");
        } catch (ScriptException e) {
            System.out.println("Error executing script: " + e.getMessage());
        }
        System.out.println(result.toString());
    }

    @Test
    public void test1() throws ScriptException, Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        engine.put("msg", "just a test");
        String str = "msg += '!!!';var user = {name:'tom',age:23,hobbies:['football','basketball']}; var name = user.name; var hb = user.hobbies[1];";
        engine.eval(str);
        String msg = (String) engine.get("msg");
        String name = (String) engine.get("name");
        String hb = (String) engine.get("hb");
        System.out.println(msg);
        System.out.println(name + ": " + hb);
    }

    @Test
    public void test2() throws ScriptException, Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        String[] keyList = new String[]{"model_高度_长度", "item_宽度Φ"};
        for (int i = 0; i < keyList.length; i++) {
            String key = keyList[i];
            String value = "";
            if (key.startsWith("model_")) {
                // 模型属性 查询模型的值 替换rule中的变量 根据modelId 和name查询
                value = "5";
            } else if (key.startsWith("item_")) {
                // 条目属性 查询条目中的属性的值 替换rule中的变量 根据itemId查询
                value = "500*500";
            }
            if (StringUtils.isBlank(value)) {
                throw new Exception("无法查询到规则引用的属性！");
            }
            engine.put(key, value);
        }
        String rule = "model_高度_长度 == 5 || item_宽度Φ == 500*500";
        System.out.println("----rule: " + rule);
        Object o = engine.eval(rule);
        System.out.println("o is " + o);
        Assert.assertTrue((Boolean) o);
        rule = String.format("if(%s) var result = 1 * 10;", rule);
        engine.eval(rule);
        Object result = engine.get("result");
        System.out.println("result: " + result);
    }
}
