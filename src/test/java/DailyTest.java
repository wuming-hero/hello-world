import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wuming.model.Account;
import com.wuming.model.Node;
import com.wuming.model.Student;
import com.wuming.model.TestModel;
import com.wuming.util.BaiduMapApi;
import com.wuming.util.GaoDeMapApi;
import com.wuming.util.HttpUtil;
import com.wuming.util.JsonMapper;
import com.wuming.util.OrderIdUtil;
import com.wuming.util.OrderNoUtil;
import com.wuming.util.Sequence;
import com.wuming.util.XmlUtil;
import com.wuming.util.XmlUtil2;
import com.wuming.util.excel.PoiExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by wuming on 16/10/30.
 * update master
 */
@Slf4j
public class DailyTest {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);

    private final ObjectMapper objectMapper = JsonMapper.nonDefaultMapper().getMapper();

    @Test
    public void dailyTest() {
        System.out.println(System.currentTimeMillis() / 1000);
        System.out.println((int) System.currentTimeMillis() / 1000);
        long curr =  System.currentTimeMillis();
        System.out.println(curr);
        System.out.println( curr / 1000);
        System.out.println((int) (curr / 1000));
        String intStr = "1738907322";
        System.out.println(Integer.parseInt(intStr));
        ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    }


    /**
     * @return List<List < String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<List<String>> readCSV(String filePath) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;
        //GBK
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "GBK"));

            CSVParser parser = CSVFormat.DEFAULT.parse(bufferedReader);
//          表内容集合，外层List为行的集合，内层List为字段集合
            List<List<String>> values = new ArrayList<>();
            int rowIndex = 0;
            for (CSVRecord record : parser.getRecords()) {
//              跳过表头
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
//              每行的内容
                List<String> value = new ArrayList<>();
                for (int i = 0; i < record.size(); i++) {

                    value.add(record.get(i));
                }
                values.add(value);
                rowIndex++;
            }
            return values;
        } catch (IOException e) {
            log.error("解析CSV内容失败" + e.getMessage(), e);
        } finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 使用main方法运行多线程
     *
     * @param args
     */
    public static void main(String[] args) {
        List<List<String>> dataList = readCSV("//Users/wuming/Downloads/trade_no_to_send_integral.csv");
        System.out.println(dataList);
        for (List<String> datas : dataList) {
            executorService.submit(() -> {
                String id = datas.get(0);
                String tradeNo = datas.get(1);
//                System.out.println("threadName : " + Thread.currentThread().getName() + ", send data, id: " + id + ", tradeNo: " + tradeNo);
                try {
                    String url = "https://app.kukr.cn/ok/order/give/integral?tradeNo=" + tradeNo;
                    String ret = HttpUtil.get(url, null);
                    System.out.println("send success, id: " + id + ", tradeNo: " + tradeNo + ", ret: " + ret);
                } catch (Exception e) {
                    System.out.println("send exception, id: " + id + ", tradeNo: " + tradeNo + ", ret: " + Throwables.getStackTraceAsString(e));
                }
            });
        }
    }

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
    public void test5() throws FileNotFoundException {
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/Users/wuming/a.exp")));
//        Stream<String> lines = bufferedReader.lines();
//        long number = lines.count();
//        lines.forEach(line-> {
//            System.out.println("1" + line);
//        });
//        System.out.println(lines.limit(10));
//        System.out.println(lines.limit(12));
//        System.out.println(lines.count());
        Date validDate = Date.from(LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(validDate);

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

    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线命名转驼峰格式命名
     */
    private static String lineToHump(String string) {
        string = string.toLowerCase();
        Matcher matcher = linePattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰格式命名转下划线格式
     *
     * @param string
     * @return
     */
    public static String humpToLine(String string) {
        Matcher matcher = humpPattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static final Long CREATE_USER_SUCCESS_ERROR_CODE = 40103L;

    @Test
    public void test7() {
        String word = "aa_bb_cc_dd";
        System.out.println(lineToHump(word));
        String code = String.valueOf(CREATE_USER_SUCCESS_ERROR_CODE);
        System.out.println(code);
    }

    @Test
    public void sortTest() {
        List<Student> studentList = new ArrayList<>(4);
        Student student = new Student();
        student.setAge(16);
        student.setName("小明16");
        studentList.add(student);

        student = new Student();
        student.setAge(16);
        student.setName("小明16-1");
        studentList.add(student);

        student = new Student();
        student.setAge(18);
        student.setName("小明18");
        studentList.add(student);

        student = new Student();
        student.setAge(17);
        student.setName("小明17");
        studentList.add(student);

        System.out.println(JSON.toJSONString(studentList));

        List<Student> sortedList = studentList.stream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(sortedList));

        List<Student> maxSortedList = studentList.stream().sorted(Comparator.comparing(Student::getAge).reversed()).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(maxSortedList));

    }

    @Test
    public void replaceTest() {
        JSONArray userIdArray = JSON.parseArray("[\"091549022126214746\",\"036308233436490420\",\"09154902191147553\",\"295868363623285786\",\"2943106357851685\",\"3723380800996542\",\"01620141421263761\",\"190833331923102269\",\"051465494232995563\",\"2633261602775803\",\"01311364680132669043\",\"196114452537914998\",\"3048506468854662\",\"030224492523449159\",\"2802492735955635\",\"172253291223500337\",\"123743554930696717\",\"0646405720774908\",\"20090058611031150\",\"01393715591252632\",\"2853170253853285\",\"140845103226407318\",\"290114505938218152\",\"295741281421034915\",\"286563172729497259\",\"01255637462521729017\",\"28561401421041531\",\"131121244220914380\",\"181659075526272041\",\"2853441344784659\",\"036613225435056584\",\"174156226438255496\",\"092154004223224129\",\"293954392032912367\",\"172144664226041271\",\"222451053720846830\",\"142347086438937589\",\"01100261151529257803\",\"01083815406321463341\",\"280106371930203470\",\"035416146724293076\",\"035554611920255535\"]");
        List<Long> userIds = new ArrayList<>(userIdArray.size());
        for (int i = 0; i < userIdArray.size(); i++) {
            userIds.add(Long.valueOf(userIdArray.get(i).toString()));
        }
        System.out.println("userId size: " + userIds.size());
        userIds = userIds.subList(0, 10);
        System.out.println(userIds);
        System.out.println("userId size: " + userIds.size());
        // 计算切片次数
        int cuteTimes = userIds.size() % 10 > 0 ? userIds.size() / 10 + 1 : userIds.size() / 10;
        System.out.println("sendTimes: " + cuteTimes);
        List<Long> subUserIds;
        for (int i = 0; i < cuteTimes; i++) {
            int startPos = i * 10;
            int endPos = Math.min(startPos + 10, userIds.size());
            subUserIds = userIds.subList(startPos, endPos);
            System.out.println(String.format("sendWorkMessageByUserId#info, startPos=%s, endPos=%s, subUserIs=%s", startPos, endPos, subUserIds));

        }
    }

    @Test
    public void replaceTest2() {
//        String urlTemplate = "pages/activity/index?q=ENCODE_URL";
//        String a = StringUtils.replaceEach(urlTemplate, new String[]{"ENCODE_URL", "SOURCE"}, new String[]{"abc", ""});
//        System.out.println(a);
//        List<String> dataList = Arrays.asList("a", "null", "", "b", "");
//        dataList = Arrays.asList("无名", "b", "蛮极");
////        System.out.println(Joiner.on(",").join(dataList));
//        System.out.println(Joiners.DUN_HAO.join(dataList));
//        LocalDate localDate = LocalDate.parse("2021-08-11", DateTimeFormat.forPattern ("yyyy-MM-dd"));
//        System.out.println(localDate);
//        System.out.println(localDate.plusDays(100));
//        String sellerAddressErrorFrameContent = "<div style=\"font-size: 17px;color: #FD3434;text-align:center;line-height: 25px\">收件人：%s</div><div style=\"font-size: 17px;color: #111111;text-align:center;line-height: 25px\">请联系商家，获取正确地址后再修改后寄出</div>";
//        String data = String.format(sellerAddressErrorFrameContent, "蛮极 13123936686 浙江省杭州市叙述区");
//        System.out.println(data);
//
//        String linkUrl = "https://h5.m.taobao.com/ww/index.htm#!dialog";
//        System.out.println(MessageFormat.format(linkUrl, Base64.getEncoder().encodeToString("蛮极".getBytes(StandardCharsets.UTF_8))));
//
//        String a = "{\"buyerId\":2212853034913,\"orderSource\":2,\"sellerId\":2211776916769,\"msgId\":\"ef8dfc2247fba88cd212998b9bb39143\",\"refundId\":147406573557034900,\"bizOrderId\":2417459689293031400,\"messageType\":\"RP-REFUND-AGRT-APPLIED\",\"bizClaimTypeEnum\":\"RETURN_AND_REFUND\"}";
//        System.out.println(JSON.parse(a));
        String address = "杭州市萧山区萧山区靖江街道保税大道西侧";
        Map<String, Double> dataMap = BaiduMapApi.getLngLat(address);
        DecimalFormat df = new DecimalFormat("#.000000");
        System.out.println(dataMap);
        System.out.println(df.format(dataMap.get("lng")));
        System.out.println(df.format(dataMap.get("lat")));

        Map<String, String> dataMap2 = GaoDeMapApi.getLngLat(address);
        System.out.println(dataMap2);
        System.out.println(dataMap2.get("lng"));
        System.out.println(dataMap2.get("lat"));

    }

    @Test
    public void excelTest() throws Exception {
        String filePath = "/Users/manji/documents/地址工作簿.xlsx";
        PoiExcelHelper excelHelper = PoiExcelHelper.create(filePath.substring(filePath.lastIndexOf(".")));

        List<String> addressList = Lists.newArrayList();
        List<String> sheetList = excelHelper.getSheetList(filePath);
        for (int i = 0; i < sheetList.size(); i++) {
            // 表头字段列表
            List<String> fieldList = new ArrayList<>();
            // 循环读取每个sheet的内容
            List<List<String>> dataList = excelHelper.readExcel(filePath, i);
            // windows上新建excel文件，默认生成3个sheet,有两个空的备用sheet
            if (dataList.isEmpty()) continue;

            // 打印表头数据，因为有合并行，最后一列为合并行标志，即标志当前单元格是否在合并行范围内，且数值代表在当前合并行的行号
            List<String> keyList = dataList.get(0);
            for (int j = 0; j < keyList.size(); j++) {
                fieldList.add(keyList.get(j));
            }
            System.out.println("---->>>>fieldList: " + fieldList);

            // 打印所有数据
            for (List<String> strings : dataList) {
//                System.out.println("count: " + strings.size() + ", data: " + strings);
                String address = strings.get(0);
                Map<String, String> dataMap = GaoDeMapApi.getLngLat(address);
                System.out.println(address + "," + dataMap.get("lng") + "," + dataMap.get("lat"));

            }
//            System.out.println("---->>>>addressSize: " + addressList.size());
//            System.out.println("---->>>>addressList: " + addressList);
        }
    }

    @Test
    public void dateTest() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        System.out.println(dateFormat.format(new Date()));
        System.out.println(Math.log10(1));

        long options = 1152921508901814272L;
        String binaryOptions = Long.toBinaryString(options);
        System.out.println(binaryOptions);
        System.out.println(StringUtils.reverse(binaryOptions));
        System.out.println(Objects.equals(String.valueOf(StringUtils.reverse(binaryOptions).charAt(60)), "1"));
        if (binaryOptions.length() < 61 || !Objects.equals(String.valueOf(StringUtils.reverse(binaryOptions).charAt(60)), "1")) {
            System.out.println(StringUtils.reverse(binaryOptions).charAt(60));
        }
        System.out.println(true);
    }

    /**
     * 媳妇产假计算
     * <p>
     * 158天产假 [8.15-2023.1.19]
     * <p>
     * 从8.15号开始，包括15号，结束时间 2023.1.19号，包括19号
     */
    @Test
    public void dateTest2() {
        Map<String, String> dataMap = Maps.newHashMap();
        dataMap.put("runTime", "123");
        dataMap.put("servername", "servername");
        dataMap.put("requestJson", "requestJson123");

        TestModel testModel = JSON.parseObject(JSON.toJSONString(dataMap), TestModel.class);
        System.out.println(testModel);
        Map<String, String> slsKeyMap = new HashMap<>();
        slsKeyMap.put("node_name", "eventCode");
        slsKeyMap.put("method_signature", "eventName");
        slsKeyMap.put("log_time", "time");
        slsKeyMap.put("out_biz_code", "orderCode");
        slsKeyMap.put("related_code", "outOrderId");
        slsKeyMap.put("request_argument", "request");
        slsKeyMap.put("rt", "runtime");
        slsKeyMap.put("rpc_id", "rpcId");
        slsKeyMap.put("trace_id", "traceId");
        System.out.println(JSON.toJSONString(slsKeyMap));

        String testStr = "\nabc{\"features\":{\"forcePce\":\"-1\",\"initialConsoWmsOutTime\":null,\"planWmsOutboundTime\":null,\"planWmsAcceptTime\":null,\"whCross\":\"false\",\"combinePriority\":\"prior\",\"tradeBatchId\":\"da59fab8fc9f3829d344a8b1f8635678\",\"initialWmsOutTime\":null},\"orderCode\":\"LP00711007763545\",\"whCrossMode\":\"-2\",\"whConsignType\":\"VIRTUAL_SHIPPING\",\"collabParcelConsignDTOList\":[{\"features\":{},\"orderCode\":\"LP00711100192844\",\"whConsignType\":\"DIRECT_SHIPPING\"}],\"cnespPlanDTO\":null}\n[";
        System.out.println(deleteInvalidPrefix(testStr));
        String testStr2 = "\nabc[{\"test\":\"242424\"}]\n111";
        System.out.println(deleteInvalidPrefix(testStr2));
        String testStr3 = "12\nabcdbakjsjf\n12";
        System.out.println(testStr3);

    }

    public static String deleteInvalidPrefix(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        // {所在的索引
        int bigBracketIndex = jsonStr.indexOf("{");
        // 中括号所在的索引
        int middleBracketIndex = jsonStr.indexOf("[");
        // 此类jsonp字符串，且没有{}
        if ((middleBracketIndex == -1 && bigBracketIndex > 0) || (bigBracketIndex == -1 && middleBracketIndex >= 0)) {
            return jsonStr.substring(Math.max(bigBracketIndex, middleBracketIndex));
        }
        // 如果2个都有
        if(bigBracketIndex > 0 && middleBracketIndex > 0){
            return jsonStr.substring(Math.min(bigBracketIndex, middleBracketIndex));
        }
        return jsonStr;
    }

    @Test
    public void subListTest() throws ParseException {
        Long time = 1733552907000L;
        // 步骤1: 定义时间区ID
        String timeZoneId = "GMT+10:30:00";

        // 步骤2: 获取TimeZone对象`
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        // 步骤3: 检查TimeZone对象是否有效
        if (timeZone != null && !timeZone.getID().equals("GMT")) { // GMT表示无效区域
            System.out.println("Time Zone ID: " + timeZone.getID());
            System.out.println("Display Name: " + timeZone.getDisplayName());
        } else {
            System.err.println("Invalid time zone ID.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        System.out.println(sdf.format(time));
    }
    public static final TimeZone cnTimeZone = TimeZone.getTimeZone("GMT+8:00");

    @Test
    public void convertBeiJingTimeTest() {
         new Date();
        // 步骤1: 定义时间区ID
        String timeZoneId = "GMT+10:30:00";
        // 步骤2: 获取TimeZone对象`
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        Date date = convertTimeZone(new Date(1732766400000L), timeZone, cnTimeZone);
        System.out.println(date);
    }




    /**
     * 将日期转换为时间戳
     *
     * <p>将给定日期转换为指定时区的时间戳
     *
     * @param date 日期对象
     * @param timeZoneStr 时区字符串
     * @return 时间戳（毫秒），如果时区字符串为空或者日期对象为null，则返回null
     */
    public static Long dateToTimeStamp(Date date, String timeZoneStr) {
        if (StringUtils.isBlank(timeZoneStr) || null == date) {
            return null;
        }
        ZoneOffset offset = ZoneOffset.of(timeZoneStr);
        ZoneId zoneId = ZoneId.from(offset);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(zoneId)
                .toInstant().toEpochMilli();
    }

    public static Date convertTimeZone(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        if (date == null) {
            return null;
        }
        long targetTime = date.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
        return new Date(targetTime);
    }

    public static boolean containsNonUTF8Character(String word) {
        try {
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.decode(ByteBuffer.wrap((word.getBytes())));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

}


