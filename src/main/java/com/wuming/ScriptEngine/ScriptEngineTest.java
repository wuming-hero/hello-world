package com.wuming.ScriptEngine;

import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 基本运行
     */
    @Test
    public void baseTest() {
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

    /**
     * 变量示例
     *
     * @throws Exception
     */
    @Test
    public void baseTest2() throws Exception {
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

    /**
     * 规则引擎计算实例
     * 变量列表中的变量名不可以有 js 中的运算符，否则报错
     *
     * @throws Exception
     */
    @Test
    public void ruleTest() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("nashorn");
        JSONArray varArray = JSONArray.fromObject("[\"model_水泥砂浆厚度\", \"item_宽度Φ\"]");

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
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("nashorn");
        JSONArray varArray = JSONArray.fromObject("[\"model_水泥砂浆厚度\", \"item_宽度Φ\"]");
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

    @Test
    public void test2(){
        Integer[] array1 = {2,3};

    }
}
