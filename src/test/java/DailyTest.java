import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuming.model.Account;
import com.wuming.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wuming on 16/10/30.
 * update master
 */
public class DailyTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    private final ObjectMapper objectMapper = JsonMapper.nonDefaultMapper().getMapper();

    /**
     * list.clone() 方法
     */
    @Test
    public void listTest() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        ArrayList<Integer> list1 = (ArrayList<Integer>) list.clone();
        System.out.println(list1);
        list.set(0, 4);
        list.add(0, 4);
        System.out.println(list);
        System.out.println(list1);
    }

    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Account account = new Account();
        account.setId(1);
        account.setName("wuming");
        System.out.println("account = " + account);

        String key = "name";
        // 通过反射使用get方法获得类Account name 属性值
        String keyGetMethod = "get" + String.valueOf(Character.toUpperCase(key.charAt(0))) + key.substring(1);
        System.out.println("key get method: " + keyGetMethod);
        Method method = account.getClass().getDeclaredMethod(keyGetMethod);
        System.out.println("value: " + method.invoke(account));

        // 循环类中所有的字段，修改字段的访问权限，然后获得其值
        Field[] fieldArray = account.getClass().getDeclaredFields();
        for (int i = 0; i < fieldArray.length; i++) {
            Field field = fieldArray[i];
            field.setAccessible(Boolean.TRUE);
            if (Objects.equals(field.getName(), key)) {
                try {
                    System.out.println(field.getName() + "=" + field.get(account));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("fieldArray = " + Arrays.asList(fieldArray));
    }

    @Test
    public void test1() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(new HashMap<String, Object>() {{
            put("name", "无名");
        }});
        list.add(new HashMap<String, Object>() {{
            put("name", "无名1");
        }});
        list.add(new HashMap<String, Object>() {{
            put("name", "无名2");
        }});
        System.out.println("before: " + list);
        list.forEach(map -> {
            if (Objects.equals(map.get("name"), "无名")) {
                System.out.println("-------------");
                map.put("name", "wuming");
            }
        });
        System.out.println("after: " + list);
    }

    /**
     * start，end表示ASCII码列表中选取字符开始和结束位置
     * 布尔型数据letters表示alphabet是否出现
     * numbers表示数字是否出现
     */
    @Test
    public void test2() {
        System.out.println("UUID：" + UUID.randomUUID().toString());
        System.out.println("" + RandomStringUtils.random(10, true, true));
        /**
         * start，end表示ASCII码列表中选取字符开始和结束位置
         * 布尔型数据letters表示alphabet是否出现
         * numbers表示数字是否出现
         */
        System.out.println("" + RandomStringUtils.random(10, 20, 110, true, true));
    }

    @Test
    public void test4() {
        String str = "山西省晋中市祁　县";
        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            System.out.println("----" + m.group());
        }
        String dest = m.replaceAll("");
        System.out.println(dest);
        new Date();
        DateTime dateTime = new DateTime();
    }

    @Test
    public void cherryPickTest() throws InterruptedException {
        Sequence sequence = new Sequence();
        Set<Long> set = new HashSet<>();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5000; i++) {
            service.submit(() -> {
                Long id = sequence.nextId();
                System.out.println(id);
                set.add(id);
            });
        }
        service.shutdown();
        service.awaitTermination(1000, TimeUnit.SECONDS);
        System.out.println(set.size());

    }

    @Test
    public void orderNoTest() throws InterruptedException {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        long t1 = new Date().getTime();
        for (int i = 0; i < 5000; i++) {
            executorService.execute(() -> {
                String num = OrderNoUtil.createOrderNo();
                map.put(num, 0);
                System.out.println("线程ID：" + Thread.currentThread().getId() + "--sequence：" + num);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);
        long t2 = new Date().getTime();
        System.out.println("使用多线程生成" + map.size() + "个订单号" + (t2 - t1));
    }

    /**
     * 生成1万个耗时 200ms
     */
    @Test
    public void orderNoTest2() {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        long t1 = new Date().getTime();
        for (int i = 0; i < 10000; i++) {
            String orderNo = OrderNoUtil.createOrderNo();
            map.put(orderNo, 0);
            System.out.println(orderNo);
        }
        long t2 = new Date().getTime();
        System.out.println("使用我线程生成" + map.size() + "个订单号" + (t2 - t1));
    }

    @Test
    public void orderIdTest() {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        long t1 = new Date().getTime();
        for (int i = 0; i < 10000; i++) {
            String orderNo = OrderIdUtil.createOrderId();
            map.put(orderNo, 0);
            System.out.println(orderNo);
        }
        long t2 = new Date().getTime();
        System.out.println("使用我线程生成" + map.size() + "个订单号" + (t2 - t1));
    }

    /**
     * 生成1万个耗时 100ms
     */
    @Test
    public void sequenceTest3() {
        long t1 = new Date().getTime();
        Sequence sequence = new Sequence();
        for (int i = 0; i < 10000; i++) {
            System.out.println(sequence.nextId());
        }
        long t2 = new Date().getTime();
        System.out.println("使用我线程生成1000个订单号" + (t2 - t1));
    }

    /**
     * java获取进程ID
     */
    @Test
    public void pidTest() throws UnknownHostException, SocketException {
        // get name representing the running Java virtual machine.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("进程名称：" + name);
        // get pid
        String pid = name.split("@")[0];
        System.out.println("进程ID：" + pid);

        // 获取线程ID
        long threadId = Thread.currentThread().getId();
        System.out.println("获取线程ID：" + threadId);

        // 获取 MAC 地址
        InetAddress ia = InetAddress.getLocalHost();
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        String macAddress = DatatypeConverter.printHexBinary(mac);
        System.out.println("macAddress：" + macAddress);

    }

    @Test
    public void xmlUtilTest() throws JsonProcessingException {
        String xmlStr = "<GYJANS><transtime>20181012174006</transtime><total>1</total><nextpage></nextpage><rds><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd></rds></GYJANS>";
        String xmlStr2 = "<GYJANS>jksfskfjskfjsfs</GYJANS>";
        String xmlStr3 = "<GYJANS><transtime>20181012174006</transtime></GYJANS>";
        System.out.println(XmlUtil2.parseXml(xmlStr2));
        System.out.println(XmlUtil2.parseXml(xmlStr3));
        System.out.println("============================");
        Map<String, Object> xmlMap2 = XmlUtil2.parseXml(xmlStr);
        System.out.println(JSON.toJSONString(xmlMap2));
    }

    @Test
    public void xmlUtil3Test() throws JsonProcessingException {
        String xmlStr = "<GYJANS><transtime>20181012174006</transtime><total>1</total><nextpage></nextpage><rds><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd></rds></GYJANS>";
        String xmlStr2 = "<GYJANS>jksfskfjskfjsfs</GYJANS>";
        String xmlStr3 = "<GYJANS><transtime>20181012174006</transtime></GYJANS>";
        String xmlStr4 = "<GYJANS><transtime>20181012174006</transtime><total>1</total><nextpage></nextpage><rds><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd></rds></GYJANS>";
        System.out.println(XmlUtil2.parseXml(xmlStr2));
        System.out.println(XmlUtil2.parseXml(xmlStr3));
        System.out.println("============================");
        Map<String, Object> xmlMap3 = XmlUtil2.parseXml(xmlStr);
        String jsonStr = JSON.toJSONString(xmlMap3);

        jsonStr = JSON.toJSONString(XmlUtil2.parseXml(xmlStr4));
        System.out.println(jsonStr);

        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        JSONObject rdsObject = jsonObject.getJSONObject("rds");
        Object rdObject = rdsObject.get("rd");
        if (rdObject instanceof JSONObject) {
            System.out.println("----" + ((JSONObject) rdObject).getString("balance"));
        }
        if (rdObject instanceof JSONArray) {
            System.out.println("++++" + ((JSONObject) ((JSONArray) rdObject).get(0)).getString("balance"));
        }

//        System.out.println(JSON.toJSONString(XmlUtil2.parseXml(xmlStr4)));
    }

    @Test
    public void xmlUtil4Test() throws JsonProcessingException {
        String xmlStr = "<GYJANS><transtime>20181012174006</transtime><total>1</total><nextpage></nextpage><rds><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd></rds></GYJANS>";
        String xmlStr2 = "<GYJANS>jksfskfjskfjsfs</GYJANS>";
        String xmlStr3 = "<GYJANS><transtime>20181012174006</transtime></GYJANS>";
        String xmlStr4 = "<GYJANS><transtime>20181012174006</transtime><total>1</total><nextpage></nextpage><rds><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd><rd><mername>宁波中惠网络科技有限公司</mername><companyname>杭州长风科技有限公司</companyname><cardno>9558853901001275150</cardno><paytime>2018-10-12 17:27:52</paytime><tranamount>0.01</tranamount><payeeacctname>支付宝（中国）网络技术有限公司客户备付金</payeeacctname><payeeacctno>33001616783059000667</payeeacctno><abstractinfo>吴豪支付宝转账</abstractinfo><operatecode>1</operatecode><balance>0.01</balance><trantype>1</trantype></rd></rds></GYJANS>";
        System.out.println(XmlUtil.xml2Json(xmlStr2));
        System.out.println(XmlUtil.xml2Json(xmlStr3));
        System.out.println("============================");
        System.out.println(XmlUtil.xml2Json(xmlStr));

        System.out.println(XmlUtil.xml2Json(xmlStr4));
    }

    @Test
    public void test5() {
        String a = "[{\"nickName\":\"给不起的未来\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166332568.jpg\"},{\"nickName\":\"一份执着。\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166838004.jpg\"},{\"nickName\":\"亡棋\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166921932.jpg\"},{\"nickName\":\"冰封de薆=\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166958993.jpg\"},{\"nickName\":\"亡心念你\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167020793.jpg\"},{\"nickName\":\"局外人\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167056215.jpg\"},{\"nickName\":\"断梦残念\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167090701.jpg\"},{\"nickName\":\"凉城\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167163725.jpg\"},{\"nickName\":\"旧城旅人\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167206019.jpg\"},{\"nickName\":\"嗜你如命\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167224791.jpg\"},{\"nickName\":\"心情杂货铺\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167270899.jpg\"},{\"nickName\":\"灬低调是种错\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167299198.jpg\"},{\"nickName\":\"人世多愁\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167321835.jpg\"},{\"nickName\":\"赖人寻味i\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167350085.jpg\"},{\"nickName\":\"不吃猫的鱼\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167371350.jpg\"},{\"nickName\":\"一个人的青春战役\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167395013.jpg\"},{\"nickName\":\"零点过十分\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167416894.jpg\"},{\"nickName\":\"试着放下丶\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167433916.jpg\"},{\"nickName\":\"看淡一切╮\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167460046.jpg\"},{\"nickName\":\"好男人不止曾小贤〃\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167478579.jpg\"},{\"nickName\":\"该如何是好\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167498544.jpg\"},{\"nickName\":\"帝国觉醒灬梦之队\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167516302.jpg\"},{\"nickName\":\"弦断心凉\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167542492.jpg\"},{\"nickName\":\"孤独的悲傷\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167576533.jpg\"},{\"nickName\":\"无敌小罗卜\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167593696.jpg\"},{\"nickName\":\"一颗心变得冰凉。\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167608947.jpg\"},{\"nickName\":\"过分宠爱\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167627575.jpg\"},{\"nickName\":\"爱情过期\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167664924.jpg\"},{\"nickName\":\"ヅ失溫℡\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167678536.jpg\"},{\"nickName\":\"不供祖宗\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167698211.jpg\"},{\"nickName\":\"繁华散尽\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167719682.jpg\"},{\"nickName\":\"被偏爱的有恃无恐\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167736405.jpg\"},{\"nickName\":\"岁月滥好人\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167757431.jpeg\"},{\"nickName\":\"不得我命\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167773325.jpeg\"},{\"nickName\":\"拿稳的你心\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167790643.jpg\"},{\"nickName\":\"心囚\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168056662.jpg\"},{\"nickName\":\"3年之约@\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168076665.jpg\"},{\"nickName\":\"暧昧\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168095560.jpg\"},{\"nickName\":\"大富豪\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168112259.jpg\"},{\"nickName\":\"有本事，别出来。\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168347194.jpg\"}]";

        String jsonStr = "[{\"nickName\":\"给不起的未来\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166332568.jpg\"},\n" +
                "{\"nickName\":\"一份执着。\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166838004.jpg\"},\n" +
                "{\"nickName\":\"亡棋\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166921932.jpg\"},\n" +
                "{\"nickName\":\"冰封de薆=\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605166958993.jpg\"},\n" +
                "{\"nickName\":\"亡心念你\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167020793.jpg\"},\n" +
                "{\"nickName\":\"局外人\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167056215.jpg\"},\n" +
                "{\"nickName\":\"断梦残念\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167090701.jpg\"},\n" +
                "{\"nickName\":\"凉城\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167163725.jpg\"},\n" +
                "{\"nickName\":\"旧城旅人\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167206019.jpg\"},\n" +
                "{\"nickName\":\"嗜你如命\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167224791.jpg\"},\n" +
                "{\"nickName\":\"心情杂货铺\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167270899.jpg\"},\n" +
                "{\"nickName\":\"灬低调是种错\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167299198.jpg\"},\n" +
                "{\"nickName\":\"人世多愁\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167321835.jpg\"},\n" +
                "{\"nickName\":\"赖人寻味i\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167350085.jpg\"},\n" +
                "{\"nickName\":\"不吃猫的鱼\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167371350.jpg\"},\n" +
                "{\"nickName\":\"一个人的青春战役\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167395013.jpg\"},\n" +
                "{\"nickName\":\"零点过十分\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167416894.jpg\"},\n" +
                "{\"nickName\":\"试着放下丶\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167433916.jpg\"},\n" +
                "{\"nickName\":\"看淡一切╮\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167460046.jpg\"},\n" +
                "{\"nickName\":\"好男人不止曾小贤〃\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167478579.jpg\"},\n" +
                "{\"nickName\":\"该如何是好\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167498544.jpg\"},\n" +
                "{\"nickName\":\"帝国觉醒灬梦之队\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167516302.jpg\"},\n" +
                "{\"nickName\":\"弦断心凉\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167542492.jpg\"},\n" +
                "{\"nickName\":\"孤独的悲傷\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167576533.jpg\"},\n" +
                "{\"nickName\":\"无敌小罗卜\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167593696.jpg\"},\n" +
                "{\"nickName\":\"一颗心变得冰凉。\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167608947.jpg\"},\n" +
                "{\"nickName\":\"过分宠爱\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167627575.jpg\"},\n" +
                "{\"nickName\":\"爱情过期\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167664924.jpg\"},\n" +
                "{\"nickName\":\"ヅ失溫℡\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167678536.jpg\"},\n" +
                "{\"nickName\":\"不供祖宗\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167698211.jpg\"},\n" +
                "{\"nickName\":\"繁华散尽\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167719682.jpg\"},\n" +
                "{\"nickName\":\"被偏爱的有恃无恐\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167736405.jpg\"},\n" +
                "{\"nickName\":\"岁月滥好人\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167757431.jpeg\"},\n" +
                "{\"nickName\":\"不得我命\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167773325.jpeg\"},\n" +
                "{\"nickName\":\"拿稳的你心\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605167790643.jpg\"},\n" +
                "{\"nickName\":\"心囚\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168056662.jpg\"},\n" +
                "{\"nickName\":\"3年之约@\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168076665.jpg\"},\n" +
                "{\"nickName\":\"暧昧\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168095560.jpg\"},\n" +
                "{\"nickName\":\"大富豪\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168112259.jpg\"},\n" +
                "{\"nickName\":\"有本事，别出来。\",\"avatar\":\"https://fula.oss-cn-hangzhou.aliyuncs.com/activity/1000001/20201112/1605168347194.jpg\"}]";
//        log.debug("---->>>>account query result: {}", jsonObject.getJSONObject("result"));
        jsonStr = jsonStr.replace("\n", "");

        JSONArray result = JSON.parseArray(jsonStr);
        System.out.println("----result: {}" + result);
        System.out.println(JSON.parseArray(a));
    }

    @Test
    public void test6() {
        String a = "覆置这行话¢mLh11Qkp6qt¢转移至\uD83D\uDC49淘宀┡ē\uD83D\uDC48【【薇娅推荐】好人家老坛酸菜鱼调料包350g*3袋酸汤鱼底料包四川】；或https://m.tb.cn/h.V6zF52Q?sm=67f245 点几鏈→接，再选择瀏lan嘂..dakai";
        String b = "覆置这行话$UQ3Z1QklMWh$转移至\uD83D\uDC49淘宀┡ē\uD83D\uDC48，【sakose凡士林海盐磨砂膏去鸡皮肤嫩白全身体去除疙瘩毛囊去角质】 还有这样的，获取中括号中间的这一部分";
        String c = "ab无名【【薇娅推荐】好人家老坛酸菜鱼调料包350g*3袋酸汤鱼底料包四川】蜗杆喳呆f ";
        // 懒惰模式，匹配第一个"【"即不再匹配
        String reg = ".*?【(.*)】.*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(b);
        if (matcher.matches()) {
            System.out.println(matcher.group(1));
        }

        System.out.println(a.replaceAll(".*?【(.*)】.*", "$1"));
        System.out.println(b.replaceAll(reg, "$1"));
        System.out.println(c.replaceAll(reg, "$1"));

        List<String> aList = new ArrayList<>(2);
        System.out.println(aList.size());

    }

}
