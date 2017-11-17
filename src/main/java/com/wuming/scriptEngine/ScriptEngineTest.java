package com.wuming.scriptEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

/**
 * Created by wuming on 2017/4/11.
 * <p>
 * Nashorn，发音“nass-horn”,是德国二战时一个坦克的命名，同时也是java8新一代的javascript引擎，
 * 替代老旧、缓慢的Rhino，符合 ECMAScript-262 5.1 版语言规范。
 * 你可能想javascript是运行在web浏览器，提供对html各种dom操作，但是Nashorn不支持浏览器DOM的对象。
 */
public class ScriptEngineTest {

    final ScriptEngineManager manager = new ScriptEngineManager();

    /**
     * 变量示例
     *
     * @throws Exception
     */
    @Test
    public void baseTest() throws Exception {
        ScriptEngine engine = manager.getEngineByName("nashorn");
        engine.put("msg", "just a test");
        String str = "msg += '!!!';var user = {name:'tom',age:23,hobbies:['football','basketball']}; var name = user.name; var hb = user.hobbies[1];";
        engine.eval(str);
        String msg = (String) engine.get("msg");
        String name = (String) engine.get("name");
        String hb = (String) engine.get("hb");
        System.out.println(msg);
        System.out.println(name + ": " + hb);

        // 2 通过 invokeFunction 调用js代码中的方法
        engine.eval("function add (a, b) {c = a + b; return c; }");
        Invocable jsInvoke = (Invocable) engine;
        Object result1 = jsInvoke.invokeFunction("add", new Object[]{10, 5});
        System.out.println("use js function: " + result1);

        // 3 getInterface来使用js实现接口中定义的方法,接口必须是public类型的
        Adder adder = jsInvoke.getInterface(Adder.class);
        int result2 = adder.add(10, 35);
        System.out.println("use java interface:" + result2);

        // 4 使用java多线程
        engine.eval("function run() {print('www.java2s.com');}");
        Invocable invokeEngine = (Invocable) engine;
        Runnable runner = invokeEngine.getInterface(Runnable.class);
        Thread t = new Thread(runner);
        t.start();
        t.join();

        // 5 jdk1.8中直接使用类的全限定名调用类中的方法
        String jsCode = "var list2 = java.util.Arrays.asList(['A', 'B', 'C']); ";
        engine.eval(jsCode);
        List<String> list2 = (List<String>) engine.get("list2");
        for (String val : list2) {
            System.out.println(val);
        }
    }


    /**
     * 规则引擎计算实例
     * 变量列表中的变量名不可以有 js 中的运算符，否则报错
     *
     * @throws Exception
     */
    @Test
    public void ruleTest() throws Exception {
        ScriptEngine scriptEngine = manager.getEngineByName("nashorn");
        JSONArray varArray = JSON.parseArray("[\"model_水泥砂浆厚度\", \"item_宽度Φ\"]");

        // 根据变量列表替换 规则 中的变量
        for (int j = 0; j < varArray.size(); j++) {
            String key = (String) varArray.get(j);
            String attrName = null;
            String attrValue = null;
            if (key.startsWith("model_")) {
                // 模型属性 查询模型的值 替换rule中的变量 根据modelId 和name查询
                attrName = key.substring(6, key.length());
                attrValue = "40";
            } else if (key.startsWith("item_")) {
                attrName = key.substring(5, key.length());
                attrValue = "25";
            }
            if (StringUtils.isBlank(attrValue)) {
                throw new Exception("无法查询到规则引用的属性！");
            }
            // 引擎变量赋值
            scriptEngine.put(key, attrValue);
        }

        String rule = "model_水泥砂浆厚度 >= 30 && item_宽度Φ >= 25";
        String expression = "var result = 10;";
        String ruleScript = String.format("if(%s){%s}", rule, expression);
        scriptEngine.eval(ruleScript);
        System.out.println(scriptEngine.get("result"));
    }

    /**
     * 规则引擎测试
     * js 特殊函数使用
     *
     * @throws Exception
     */
    @Test
    public void ruleTest2() throws Exception {
        ScriptEngine scriptEngine = manager.getEngineByName("nashorn");
        JSONArray varArray = JSON.parseArray("[\"model_水泥砂浆厚度\", \"item_宽度Φ\"]");
        // 根据变量列表替换 规则 中的变量
        for (int j = 0; j < varArray.size(); j++) {
            String key = (String) varArray.get(j);
            String attrName = null;
            String attrValue = null;
            if (key.startsWith("model_")) {
                // 模型属性 查询模型的值 替换rule中的变量 根据modelId 和name查询
                attrName = key.substring(6, key.length());
                attrValue = "1.4";
            } else if (key.startsWith("item_")) {
                attrName = key.substring(5, key.length());
                attrValue = "2.6";
            }
            if (StringUtils.isBlank(attrValue)) {
                throw new Exception("无法查询到规则引用的属性！");
            }
            // 引擎变量赋值
            scriptEngine.put(key, attrValue);
        }
        // sin、cos 函数使用 sin(x)、cos(x)的参数为弧度，弧度与角度换算 弧度 = 2 * PI / 360 * 角度;
        String rule = "Math.sin(model_水泥砂浆厚度) >= 0.5 && Math.cos(item_宽度Φ) < 0.5";
        // 向上取整、向下取整
        rule = "Math.ceil(model_水泥砂浆厚度 / 10) * 10 >= 2 && Math.floor(item_宽度Φ / 10) * 10 < 3";
        String expression = "var result = 10;";
        String ruleScript = String.format("if(%s){%s}", rule, expression);
        scriptEngine.eval(ruleScript);
        System.out.println(scriptEngine.get("result"));
    }
}
