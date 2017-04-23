package com.wuming.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.wuming.model.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuming on 2017/4/13.
 */
public class Jackson {

    private ObjectMapper objectMapper = null;
    private JsonGenerator jsonGenerator = null;
    private Account account;
    private String path = Jackson.class.getResource("/file/test.json").getPath();
    File file = new File(path);

    @Before
    public void init() {
        account = new Account();
        account.setId(1);
        account.setName("wuming");
        account.setEmail("wuming@qq.com");
        account.setAddress("杭州市");

        objectMapper = new ObjectMapper();
        try {
            jsonGenerator = objectMapper.getFactory().createGenerator(System.out, JsonEncoding.UTF8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void destroy() {
        try {
            if (jsonGenerator != null) {
                jsonGenerator.flush();
            }
            if (!jsonGenerator.isClosed()) {
                jsonGenerator.close();
            }
            jsonGenerator = null;
            objectMapper = null;
            account = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 利用JsonGenerator的writeObject方法和ObjectMapper的writeValue方法完成对Java对象的转换，二者传递的参数及构造的方式不同；
     * JsonGenerator的创建依赖于ObjectMapper对象。也就是说如果你要使用JsonGenerator来转换JSON，那么你必须创建一个ObjectMapper。
     * 但是你用ObjectMapper来转换JSON，则不需要JSONGenerator。
     * <p>
     * objectMapper的writeValue方法可以将一个Java对象转换成JSON。
     * 第一个参数，需要提供一个输出流，转换后可以通过这个流来输出转换后的内容。或是提供一个File，将转换后的内容写入到File中。当然，这个参数也可以接收一个JSONGenerator，然后通过JSONGenerator来输出转换后的信息。
     * 第二个参数是将要被转换的Java对象。
     * 如果用三个参数的方法，那么是一个Config。这个config可以提供一些转换时的规则，过指定的Java对象的某些属性进行过滤或转换等。
     */
    @Test
    public void bean2json() {
        try {
            System.out.println("jsonGenerator");
//            writeObject可以转换java对象，eg:JavaBean/Map/List/Array等
            jsonGenerator.writeObject(account);
//            System.out.println("ObjectMapper");
////            writeValue具有和writeObject相同的功能
//            objectMapper.writeValue(System.out, account);

            String jsonStr = objectMapper.writeValueAsString(account);
            System.out.println("jsonStr: " + jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void map2json() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "无名");
        map.put("age", 26);
        List<String> hobbies = new ArrayList<>();
        hobbies.add("篮球");
        hobbies.add("乒乓球");
        map.put("hobbies", hobbies);
        Map<String, Object> englishName = new HashMap<>();
        englishName.put("first name", "yang");
        englishName.put("last name", "pengpeng");
        map.put("englishName", englishName);
        String jsonStr = objectMapper.writeValueAsString(map);
        System.out.println("jsonStr: " + jsonStr);
    }

    /**
     * 读写文件
     *
     * @throws IOException
     */
    @Test
    public void readJsonFile() throws IOException {
        System.out.println("resource path: " + path);
        objectMapper.writeValue(file, account);
        // 从文件中读取json
        JsonNode rootNode = objectMapper.readTree(file);
        System.out.println("jsonStr: " + rootNode.toString());
        // 读取出 map
        Map<String, Object> map = objectMapper.readValue(file, Map.class);
        System.out.println("map: " + map);
        // 读取出 bean
        Account bean = objectMapper.readValue(file, Account.class);
        System.out.println("name: " + bean.getEmail());
    }

    /**
     * 使用 TypeReference 进行类型转换
     *
     * @throws IOException
     */
    @Test
    public void typeReferenceTest() throws IOException {
        Map<String, Account> result = objectMapper.readValue(file, new TypeReference<Map<String, Account>>() {
        });
        Account bean = result.get("test");
        System.out.println(bean.getName());

        String jsonStr = "[1,2,3,4,5]";
        List<Integer> numberList = objectMapper.readValue(jsonStr, List.class);
        System.out.println(numberList);
        List<Long> numberList2 = objectMapper.readValue(jsonStr, new TypeReference<List<Long>>() {
        });
        System.out.println(numberList2);
    }

    @Test
    public void writeOthersJSON() throws IOException {
        String[] arr = {"a", "b", "c"};
        System.out.println("jsonGenerator");
        String str = "hello world jackson!";
        //byte
        jsonGenerator.writeBinary(str.getBytes());
        //boolean
        jsonGenerator.writeBoolean(true);
        //null
        jsonGenerator.writeNull();
        //float
        jsonGenerator.writeNumber(2.2f);
        //char
        jsonGenerator.writeRaw("c");
        //String
        jsonGenerator.writeRaw(str, 5, 10);
        //String
        jsonGenerator.writeRawValue(str, 5, 5);
        //String
        jsonGenerator.writeString(str);
        jsonGenerator.writeTree(JsonNodeFactory.instance.pojoNode(str));
        System.out.println();

        //Object
        jsonGenerator.writeStartObject();//{
        jsonGenerator.writeObjectFieldStart("user");//user:{
        jsonGenerator.writeStringField("name", "jackson");//name:jackson
        jsonGenerator.writeBooleanField("sex", true);//sex:true
        jsonGenerator.writeNumberField("age", 22);//age:22
        jsonGenerator.writeEndObject();//}

        jsonGenerator.writeArrayFieldStart("infos");//infos:[
        jsonGenerator.writeNumber(22);//22
        jsonGenerator.writeString("this is array");//this is array
        jsonGenerator.writeEndArray();//]
        jsonGenerator.writeEndObject();//}

        //complex Object
        jsonGenerator.writeStartObject();//{
        jsonGenerator.writeObjectField("user", account);//user:{bean}
        jsonGenerator.writeObjectField("infos", arr);//infos:[array]
        jsonGenerator.writeEndObject();//}
    }

    /**
     * 借助扩展插件jackson-dataformat-xml将 bean map 写到 转换成 xml
     *
     */
    @Test
    public void writeObject2Xml() {
        System.out.println("XmlMapper");
        XmlMapper xml = new XmlMapper();
        try {
            //javaBean转换成xml
            //xml.writeValue(System.out, bean);
            StringWriter sw = new StringWriter();
            xml.writeValue(sw, account);
            System.out.println(sw.toString());

            //List转换成xml
            List<Account> list = new ArrayList<>();
            list.add(account);
            list.add(account);
            System.out.println(xml.writeValueAsString(list));

            //Map转换xml文档
            Map<String, Account> map = new HashMap<>();
            map.put("A", account);
            map.put("B", account);
            System.out.println(xml.writeValueAsString(map));

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
