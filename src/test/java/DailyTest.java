import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.osgi.WstxBundleActivator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.tools.corba.se.idl.StringGen;
import com.wuming.model.Account;
import com.wuming.model.Student;
import com.wuming.util.*;
import com.wuming.util.excel.PoiExcel2k7Helper;
import com.wuming.util.excel.PoiExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import sun.jvm.hotspot.code.StubQueue;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wuming on 16/10/30.
 * update master
 */
@Slf4j
public class DailyTest {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);

    private final ObjectMapper objectMapper = JsonMapper.nonDefaultMapper().getMapper();

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
    public void dateTest2() throws InvocationTargetException, IllegalAccessException {
//        LocalDate localDate = LocalDate.of(2022, 8, 10);
//        System.out.println(localDate);
//        LocalDate targetDay = localDate.plusDays(158);
//        System.out.println(targetDay);
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.minusDays(30));

        List<String> dataList = null;
//        for (String s : dataList) {
//            System.out.println(s);
//        }

        List<String> dataList2 = new ArrayList<>(dataList);
        System.out.println(dataList2);
    }

    @Test
    public void subListTest() throws ParseException {
//        List<Integer> dataList = Arrays.asList(1, 2, 3, 4, 5);
//        System.out.println(dataList.subList(0, dataList.size()));
//        String data = String.format("%s:%s", null, null);
//        System.out.println(data);
////        System.out.println(NumberUtils.isCreatable("1.23111"));
////        System.out.println(NumberUtils.isCreatable("0.23"));
////        System.out.println(NumberUtils.isCreatable("0.23"));
//
//        System.out.println(NumberUtils.isParsable("-.11"));
//        System.out.println(NumberUtils.isParsable("1.23111"));
//        System.out.println(NumberUtils.isParsable("-0.23"));
//        System.out.println(NumberUtils.isParsable("0.23"));
//        System.out.println(new BigDecimal("-.11").multiply(new BigDecimal(100)).longValue());
//
////        ZoneId zoneId1 = ZoneId.of("+8");
////        ZoneId zoneId2 = ZoneId.of("+8:00");
////        System.out.println(zoneId1.equals(zoneId2));
////        ZoneOffset zoneOffset = ZoneOffset.of("8");
////        ZoneOffset zoneOffset2 = ZoneOffset.of("+8");
////        ZoneOffset zoneOffset3 = ZoneOffset.of("+08:00");
//
//        for (int i = 0; i < 1000; i++) {
//            System.out.println(RandomUtils.nextInt(0, 2));
//
//        }

        String configInfo = "{\n" +
                "   \"UNREACHABLE_RETURN_START_CONFIG\": {\n" +
                "      \"AE_OVERSEA_WAREHOUSE_CONSIGN\": \"OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"AE_SELF_SUPPORT_SPAIN\": \"OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"AE_SELF_SUPPORT\": \"OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"AE_SELF_SUPPORT_STATION\": \"OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"AE_RU_OVERSEA_GD\": \"OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"AE_RU_MARKETPLACE_PUDO\": \"OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"AE_4PL_SPAIN_FMCG\": \"NEW_OVERSEA_UNREACHABLE_RETURN_REQUEST\",\n" +
                "      \"DEFAULT\": \"UNREACHABLE_RETURN_REQUEST\"\n" +
                "   },\n" +
                "   \"OPEN_GRAY_ROUTE\": true,\n" +
                "   \"GRAY_COUNTRY_ID_LIST\": [\n" +
                "      \"32\",\n" +
                "      \"174\",\n" +
                "      \"134\"\n" +
                "   ],\n" +
                "   \"GRAY_CP_CODE_LIST\": [\n" +
                "      \"DISTRIBUTOR_30550506\",\n" +
                "      \"DISTRIBUTOR_30551634\",\n" +
                "      \"DISTRIBUTOR_30518537\",\n" +
                "      \"DISTRIBUTOR_1366684\",\n" +
                "      \"DISTRIBUTOR_30305166\",\n" +
                "      \"DISTRIBUTOR_13422877\",\n" +
                "      \"DISTRIBUTOR_13507140\",\n" +
                "      \"DISTRIBUTOR_30477762\",\n" +
                "      \"DISTRIBUTOR_30223562\",\n" +
                "      \"DISTRIBUTOR_30284443\",\n" +
                "      \"FPXSG\",\n" +
                "      \"DISTRIBUTOR_1216244\",\n" +
                "      \"DISTRIBUTOR_30092388\",\n" +
                "      \"DISTRIBUTOR_11991627\",\n" +
                "      \"DISTRIBUTOR_30253977\",\n" +
                "      \"DISTRIBUTOR_30405541\",\n" +
                "      \"DISTRIBUTOR_13421799\",\n" +
                "      \"DISTRIBUTOR_30425636\",\n" +
                "      \"DISTRIBUTOR_30112472\",\n" +
                "      \"DISTRIBUTOR_30506448\",\n" +
                "      \"DISTRIBUTOR_30425560\",\n" +
                "      \"GATE_30224341\",\n" +
                "      \"GATE_30088431\",\n" +
                "      \"GATE_30523872\",\n" +
                "      \"GATE_13506798\"\n" +
                "   ],\n" +
                "   \"UNREACHABLE_CONSO_CODE_LIST\": [\n" +
                "      \"TRAN_STORE_30127724\",\n" +
                "      \"TRAN_STORE_30463958\",\n" +
                "      \"TRAN_STORE_30554302\",\n" +
                "      \"TRAN_STORE_30553909\",\n" +
                "      \"TRAN_STORE_30553130\",\n" +
                "      \"TRAN_STORE_30554115\",\n" +
                "      \"TRAN_STORE_30471444\",\n" +
                "      \"TRAN_STORE_30284913\",\n" +
                "      \"TRAN_STORE_30320469\",\n" +
                "      \"TRAN_STORE_30874365\",\n" +
                "      \"TRAN_STORE_31116966\",\n" +
                "      \"TRAN_STORE_31135158\",\n" +
                "      \"TRAN_STORE_30435089\",\n" +
                "      \"TRAN_STORE_31353997\"\n" +
                "   ],\n" +
                "   \"GRAY_CP_CODE_MAP\": {\n" +
                "      \"32\": [\n" +
                "         \"DISTRIBUTOR_30319743\",\n" +
                "         \"DISTRIBUTOR_30223562\",\n" +
                "         \"DISTRIBUTOR_30284443\",\n" +
                "         \"DISTRIBUTOR_1366684\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30784974\",\n" +
                "         \"GATE_30507025\",\n" +
                "         \"TRAN_STORE_30704588\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"GATE_30937793\",\n" +
                "         \"GATE_30937583\",\n" +
                "         \"DISTRIBUTOR_30506357\",\n" +
                "         \"DISTRIBUTOR_30506438\",\n" +
                "         \"DISTRIBUTOR_30506837\",\n" +
                "         \"DISTRIBUTOR_30507164\"\n" +
                "      ],\n" +
                "      \"33\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"174\": [\n" +
                "         \"DISTRIBUTOR_30305166\",\n" +
                "         \"DISTRIBUTOR_30092388\",\n" +
                "         \"DISTRIBUTOR_11991627\",\n" +
                "         \"DISTRIBUTOR_30425636\",\n" +
                "         \"DISTRIBUTOR_30112472\",\n" +
                "         \"DISTRIBUTOR_30425560\",\n" +
                "         \"GATE_30224341\",\n" +
                "         \"GATE_30088431\",\n" +
                "         \"GATE_30523872\",\n" +
                "         \"DISTRIBUTOR_13422877\",\n" +
                "         \"DISTRIBUTOR_13507140\",\n" +
                "         \"DISTRIBUTOR_30477762\",\n" +
                "         \"DISTRIBUTOR_30518537\",\n" +
                "         \"DISTRIBUTOR_30253977\",\n" +
                "         \"DISTRIBUTOR_30405541\",\n" +
                "         \"DISTRIBUTOR_13421799\",\n" +
                "         \"DISTRIBUTOR_30506448\",\n" +
                "         \"GATE_13506798\",\n" +
                "         \"TRAN_STORE_30149953\",\n" +
                "         \"TRAN_STORE_30258519\",\n" +
                "         \"TRAN_STORE_30412160\",\n" +
                "         \"TRAN_STORE_30407252\",\n" +
                "         \"DISTRIBUTOR_30792239\"\n" +
                "      ],\n" +
                "      \"134\": [\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_30550506\",\n" +
                "         \"DISTRIBUTOR_30551634\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30784388\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"185\": [\n" +
                "         \"DISTRIBUTOR_30832844\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_30695375\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"GATE_31023955\",\n" +
                "         \"DISTRIBUTOR_31023097\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_31199230\",\n" +
                "         \"DISTRIBUTOR_31023097\",\n" +
                "         \"DISTRIBUTOR_31043296\",\n" +
                "         \"DISTRIBUTOR_31264672\",\n" +
                "         \"DISTRIBUTOR_31052471\",\n" +
                "         \"DISTRIBUTOR_30884786\",\n" +
                "         \"GATE_31200160\"\n" +
                "      ],\n" +
                "      \"224\": [\n" +
                "         \"DISTRIBUTOR_30696279\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30832845\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_31171074\"\n" +
                "      ],\n" +
                "      \"19\": [\n" +
                "         \"DISTRIBUTOR_30715650\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"159\": [\n" +
                "         \"DISTRIBUTOR_30716188\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"153\": [\n" +
                "         \"DISTRIBUTOR_30521976\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"141\": [\n" +
                "         \"DISTRIBUTOR_30521976\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"109\": [\n" +
                "         \"DISTRIBUTOR_30521976\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"78\": [\n" +
                "         \"DISTRIBUTOR_30521976\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"16\": [\n" +
                "         \"DISTRIBUTOR_31094522\",\n" +
                "         \"GATE_31093480\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30986012\",\n" +
                "         \"GATE_30986121\",\n" +
                "         \"GATE_30986121\",\n" +
                "         \"DISTRIBUTOR_30986012\",\n" +
                "         \"GATE_31171271\",\n" +
                "         \"DISTRIBUTOR_31216234\"\n" +
                "      ],\n" +
                "      \"17\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"228\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30792239\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"TRAN_STORE_30407252\",\n" +
                "         \"TRAN_STORE_30149953\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"190\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30676796\"\n" +
                "      ],\n" +
                "      \"37\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_31108882\",\n" +
                "         \"DISTRIBUTOR_31191299\",\n" +
                "         \"DISTRIBUTOR_31200225\",\n" +
                "         \"DISTRIBUTOR_31166021\",\n" +
                "         \"TRAN_STORE_31345344\",\n" +
                "         \"DISTRIBUTOR_31425044\"\n" +
                "      ],\n" +
                "      \"100\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_30474040\",\n" +
                "         \"DISTRIBUTOR_30134774\"\n" +
                "      ],\n" +
                "      \"95\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"173\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"98\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"150\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30782201\"\n" +
                "      ],\n" +
                "      \"6\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"128\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"158\": [\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"209\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30460633\",\n" +
                "         \"TRAN_STORE_30552669\"\n" +
                "      ],\n" +
                "      \"104\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"GATE_30652901\",\n" +
                "         \"DISTRIBUTOR_30652654\",\n" +
                "         \"GATE_30778947\",\n" +
                "         \"DISTRIBUTOR_30778660\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30320559\",\n" +
                "         \"DISTRIBUTOR_30792239\"\n" +
                "      ],\n" +
                "      \"160\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30706214\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"172\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"206\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30728886\",\n" +
                "         \"DISTRIBUTOR_31029250\"\n" +
                "      ],\n" +
                "      \"170\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"5\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"111\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"46\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30696771\",\n" +
                "         \"DISTRIBUTOR_30785301\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"200\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30792425\",\n" +
                "         \"DISTRIBUTOR_30945536\"\n" +
                "      ],\n" +
                "      \"9\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"142\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"218\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"198\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_13489152\",\n" +
                "         \"DISTRIBUTOR_30876223\",\n" +
                "         \"DISTRIBUTOR_30943840\",\n" +
                "         \"GATE_30874427\",\n" +
                "         \"GATE_13486697\",\n" +
                "         \"TRAN_STORE_31345344\"\n" +
                "      ],\n" +
                "      \"233\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30721631\",\n" +
                "         \"TRAN_STORE_30552669\"\n" +
                "      ],\n" +
                "      \"13\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"71\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"43\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"165\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30785235\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"125\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30753581\"\n" +
                "      ],\n" +
                "      \"226\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"188\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"120\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"121\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_31089825\",\n" +
                "         \"DISTRIBUTOR_30943096\"\n" +
                "      ],\n" +
                "      \"63\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"144\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"147\": [\n" +
                "         \"DISTRIBUTOR_30874400\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874413\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_30874400\",\n" +
                "         \"DISTRIBUTOR_30888815\",\n" +
                "         \"DISTRIBUTOR_13474562\",\n" +
                "         \"DISTRIBUTOR_30943096\",\n" +
                "         \"DISTRIBUTOR_31198598\",\n" +
                "         \"DISTRIBUTOR_31037258\",\n" +
                "         \"DISTRIBUTOR_31089624\"\n" +
                "      ],\n" +
                "      \"229\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"101\": [\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_31066034\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"TRAN_STORE_30986541\",\n" +
                "         \"DISTRIBUTOR_30528050\",\n" +
                "         \"DISTRIBUTOR_30441528\",\n" +
                "         \"DISTRIBUTOR_30675922\",\n" +
                "         \"DISTRIBUTOR_30889763\",\n" +
                "         \"DISTRIBUTOR_30888815\"\n" +
                "      ],\n" +
                "      \"196\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"112\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_13331996\"\n" +
                "      ],\n" +
                "      \"171\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_31089836\",\n" +
                "         \"DISTRIBUTOR_30278133\"\n" +
                "      ],\n" +
                "      \"102\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"93\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"132\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"108\": [\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"CHINA_TRAN_STORE_30488682\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_30278133\",\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"TRAN_STORE_31415927\"\n" +
                "      ],\n" +
                "      \"240\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"107\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"TRAN_STORE_31010459\"\n" +
                "      ],\n" +
                "      \"60\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"123\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"86\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"152\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"74\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"67\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"23\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30278133\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"TRAN_STORE_31308072\"\n" +
                "      ],\n" +
                "      \"76\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30278133\",\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_13331996\"\n" +
                "      ],\n" +
                "      \"217\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"254\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"14\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30278133\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"64\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"242\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"39\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"187\": [\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"18\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30278133\"\n" +
                "      ],\n" +
                "      \"117\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"50\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"51\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"53\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"230\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30278133\"\n" +
                "      ],\n" +
                "      \"3\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"183\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"129\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"110\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"124\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"89\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"179\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"184\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"213\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"194\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"215\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"227\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"127\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"12\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"2\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"113\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"114\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"212\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"42\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"211\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"177\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"195\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"249\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"225\": [\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"1\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"TRAN_STORE_31308072\"\n" +
                "      ],\n" +
                "      \"122\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"62\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"15\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"10\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"210\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"27\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"157\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"169\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"34\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_1216244\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"59\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"252\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"205\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"68\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"48\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"20\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"246\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"148\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"87\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"99\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"140\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"135\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"154\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"155\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"161\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"182\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"241\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"244\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"191\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"192\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"193\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"180\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"181\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"203\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"220\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"216\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"236\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"243\": [\n" +
                "         \"FPXSG\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"234\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"235\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"7\": [\n" +
                "         \"FPXSG\"\n" +
                "      ],\n" +
                "      \"77\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30544403\",\n" +
                "         \"DISTRIBUTOR_30544414\",\n" +
                "         \"DISTRIBUTOR_30544376\",\n" +
                "         \"DISTRIBUTOR_31052829\",\n" +
                "         \"DISTRIBUTOR_30825132\",\n" +
                "         \"DISTRIBUTOR_31098259\",\n" +
                "         \"DISTRIBUTOR_30744258\",\n" +
                "         \"DISTRIBUTOR_12008647\",\n" +
                "         \"DISTRIBUTOR_31089485\",\n" +
                "         \"DISTRIBUTOR_30796353\",\n" +
                "         \"DISTRIBUTOR_31020079\",\n" +
                "         \"DISTRIBUTOR_30889790\"\n" +
                "      ],\n" +
                "      \"65\": [\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"66\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"24\": [\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30943096\",\n" +
                "         \"DISTRIBUTOR_30671493\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_31101485\",\n" +
                "         \"DISTRIBUTOR_30888815\"\n" +
                "      ],\n" +
                "      \"223\": [\n" +
                "         \"TRAN_STORE_30552669\",\n" +
                "         \"DISTRIBUTOR_30874813\",\n" +
                "         \"DISTRIBUTOR_30541618\",\n" +
                "         \"DISTRIBUTOR_30536679\",\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_13480256\",\n" +
                "         \"DISTRIBUTOR_13480265\",\n" +
                "         \"DISTRIBUTOR_30116091\",\n" +
                "         \"DISTRIBUTOR_31089606\",\n" +
                "         \"DISTRIBUTOR_31096841\"\n" +
                "      ],\n" +
                "      \"199\": [\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30438883\",\n" +
                "         \"DISTRIBUTOR_30526399\",\n" +
                "         \"DISTRIBUTOR_30250031\",\n" +
                "         \"DISTRIBUTOR_30527138\",\n" +
                "         \"DISTRIBUTOR_30748369\",\n" +
                "         \"DISTRIBUTOR_30749306\",\n" +
                "         \"DISTRIBUTOR_30462294\",\n" +
                "         \"DISTRIBUTOR_30766261\",\n" +
                "         \"DISTRIBUTOR_30294626\",\n" +
                "         \"DISTRIBUTOR_30749686\",\n" +
                "         \"DISTRIBUTOR_30129706\",\n" +
                "         \"DISTRIBUTOR_30438000\",\n" +
                "         \"DISTRIBUTOR_13453351\",\n" +
                "         \"DISTRIBUTOR_30884976\",\n" +
                "         \"DISTRIBUTOR_30526252\",\n" +
                "         \"DISTRIBUTOR_31034751\",\n" +
                "         \"DISTRIBUTOR_31035063\",\n" +
                "         \"DISTRIBUTOR_30526184\",\n" +
                "         \"DISTRIBUTOR_30749178\",\n" +
                "         \"DISTRIBUTOR_31036065\",\n" +
                "         \"AE_HUB_L300000351\"\n" +
                "      ],\n" +
                "      \"80\": [\n" +
                "         \"DISTRIBUTOR_30939910\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"167\": [\n" +
                "         \"DISTRIBUTOR_30133106\",\n" +
                "         \"DISTRIBUTOR_30776027\",\n" +
                "         \"DISTRIBUTOR_30887461\",\n" +
                "         \"DISTRIBUTOR_13463001\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30888477\"\n" +
                "      ],\n" +
                "      \"92\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30476027\",\n" +
                "         \"DISTRIBUTOR_30776027\",\n" +
                "         \"DISTRIBUTOR_30886918\",\n" +
                "         \"DISTRIBUTOR_30442375\"\n" +
                "      ],\n" +
                "      \"54\": [\n" +
                "         \"DISTRIBUTOR_30298106\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30874813\"\n" +
                "      ],\n" +
                "      \"56\": [\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"168\": [\n" +
                "         \"DISTRIBUTOR_30777681\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_31206438\",\n" +
                "         \"AE_HUB_L300000202\",\n" +
                "         \"DISTRIBUTOR_31206438\"\n" +
                "      ],\n" +
                "      \"72\": [\n" +
                "         \"DISTRIBUTOR_30874367\",\n" +
                "         \"DISTRIBUTOR_30874247\",\n" +
                "         \"DISTRIBUTOR_30874547\",\n" +
                "         \"DISTRIBUTOR_30874723\",\n" +
                "         \"DISTRIBUTOR_30728660\",\n" +
                "         \"DISTRIBUTOR_30937534\",\n" +
                "         \"DISTRIBUTOR_30223590\",\n" +
                "         \"DISTRIBUTOR_30728948\",\n" +
                "         \"DISTRIBUTOR_30728966\",\n" +
                "         \"DISTRIBUTOR_30789261\",\n" +
                "         \"DISTRIBUTOR_30492988\",\n" +
                "         \"DISTRIBUTOR_30798142\",\n" +
                "         \"DISTRIBUTOR_30735881\",\n" +
                "         \"DISTRIBUTOR_30885735\",\n" +
                "         \"DISTRIBUTOR_30885925\",\n" +
                "         \"DISTRIBUTOR_30879677\",\n" +
                "         \"DISTRIBUTOR_30941646\",\n" +
                "         \"DISTRIBUTOR_30748180\",\n" +
                "         \"DISTRIBUTOR_30788028\",\n" +
                "         \"DISTRIBUTOR_30788723\",\n" +
                "         \"DISTRIBUTOR_30424555\",\n" +
                "         \"DISTRIBUTOR_30438148\",\n" +
                "         \"DISTRIBUTOR_30443043\",\n" +
                "         \"DISTRIBUTOR_13492249\",\n" +
                "         \"DISTRIBUTOR_31089827\",\n" +
                "         \"DISTRIBUTOR_31089364\",\n" +
                "         \"DISTRIBUTOR_31089663\",\n" +
                "         \"DISTRIBUTOR_31089715\"\n" +
                "      ]\n" +
                "   },\n" +
                "   \"GENERATE_WILL_BILL_ERROR_CODE_OF_REFUND\": [\n" +
                "      \"consigneePhone and consigneeMobile can not be both null\",\n" +
                "      \"phone of consignee too long\",\n" +
                "      \"mobile of consignee too long\",\n" +
                "      \"name of consignee too long\",\n" +
                "      \"province of consigneeAddress can not be null\",\n" +
                "      \"consignee_address_area_too_long\",\n" +
                "      \"detail consigneeAddress can not be null\",\n" +
                "      \"consignee_address_city_too_long\",\n" +
                "      \"consignee_address_province_too_long\",\n" +
                "      \"consignee address too long\",\n" +
                "      \"consignee_address_town_too_long\",\n" +
                "      \"consignee_address_detail_too_long\",\n" +
                "      \"detail consigneeAddress has invalid character\",\n" +
                "      \"name of consignee has invalid character\",\n" +
                "      \"phone or mobile illegal\"\n" +
                "   ],\n" +
                "   \"REFUND_ERROR_OP_REMARK\": [\n" +
                "      \"130^^^联系不上客户\",\n" +
                "      \"1711^^^客户地址错误\",\n" +
                "      \"1004^^^客户更改配送地址\",\n" +
                "      \"1710^^^拒收-客户-其它原因\",\n" +
                "      \"SEC_CODE^^^130\",\n" +
                "      \"SEC_CODE^^^1002\",\n" +
                "      \"SEC_CODE^^^1711\",\n" +
                "      \"SEC_CODE^^^3004\",\n" +
                "      \"SEC_CODE^^^3005\"\n" +
                "   ],\n" +
                "   \"OVERSEAS_RETURN_SORTING_CENTER_RES_CODE_LIST\": [\n" +
                "      \"TRAN_STORE_30716478\",\n" +
                "      \"TRAN_STORE_30534825\",\n" +
                "      \"Tran_Store_11955923\",\n" +
                "      \"Tran_Store_13459842\",\n" +
                "      \"Tran_Store_11929262\"\n" +
                "   ],\n" +
                "   \"SINOTRANS_SORTING_CENTER_RES_CODE\": [\n" +
                "      \"TRAN_STORE_30986541\",\n" +
                "      \"TRAN_STORE_30886279\",\n" +
                "      \"Tran_Store_11955923\",\n" +
                "      \"TRAN_STORE_30835544\"\n" +
                "   ],\n" +
                "   \"OVERDUE_RETURN_IS_NO_SUPPORT_RES_CODE\": [\n" +
                "      \"TRAN_STORE_30705521\"\n" +
                "   ],\n" +
                "   \"START_RETURN_RES_EXPRESS_RES_MAP\": {\n" +
                "      \"Tran_Store_13497362\": \"DISTRIBUTOR_30439715\"\n" +
                "   },\n" +
                "   \"EXCLUDE_TOTAL_AMOUNT_RES_CODE\": [\n" +
                "      \"Tran_Store_11955923\",\n" +
                "      \"TRAN_STORE_30534825\",\n" +
                "      \"TRAN_STORE_30886279\"\n" +
                "   ],\n" +
                "   \"DOMESTIC_DELIVERY_ACCESS_STO_RES_CODE\": [\n" +
                "      \"TRAN_1_STORE_30299685\",\n" +
                "      \"TRAN_1_STORE_30320880\",\n" +
                "      \"TRAN_1_STORE_30790506\"\n" +
                "   ],\n" +
                "   \"PRINT_DATA_TEMPLATE_URL\": {\n" +
                "      \"DISTRIBUTOR_30439715\": \"http://cloudprint.cainiao.com/template/standard/420313/27\",\n" +
                "      \"DISTRIBUTOR_31078355\": \"https://cloudprint.cainiao.com/template/standard/643524/1\"\n" +
                "   },\n" +
                "   \"DOMESITC_DELIVERY_RES_CODE_TO_CP_CODE\": {\n" +
                "      \"DISTRIBUTOR_30439715\": \"YTO\",\n" +
                "      \"DISTRIBUTOR_31078355\": \"STO\"\n" +
                "   },\n" +
                "   \"ABNORMAL_CODE_INFO_LIST\": [\n" +
                "      {\n" +
                "         \"opCode\": \"5225\",\n" +
                "         \"remark\": \"IPR问题\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5280\",\n" +
                "         \"remark\": \"禁运品：毒性物质（毒品、食品、药品、活体）\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5277\",\n" +
                "         \"remark\": \"禁运品：管制器具\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5276\",\n" +
                "         \"remark\": \"禁运品：枪支弹药\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5279\",\n" +
                "         \"remark\": \"禁运品：压缩容器（含打火机、电子烟等）\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5291\",\n" +
                "         \"remark\": \"禁运品：易燃易爆\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5281\",\n" +
                "         \"remark\": \"禁运品：纸币、色子、公章、筹码等\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5243\",\n" +
                "         \"remark\": \"品类限制：纯电\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5242\",\n" +
                "         \"remark\": \"品类限制：带电\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5275\",\n" +
                "         \"remark\": \"品类限制：磁性物品\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5270\",\n" +
                "         \"remark\": \"品类限制：电容类\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5272\",\n" +
                "         \"remark\": \"品类限制：粉末\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5273\",\n" +
                "         \"remark\": \"品类限制：膏状体\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5269\",\n" +
                "         \"remark\": \"品类限制：化工品类\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5244\",\n" +
                "         \"remark\": \"品类限制：化妆品\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5271\",\n" +
                "         \"remark\": \"品类限制：液体\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5264\",\n" +
                "         \"remark\": \"申报金额问题\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5258\",\n" +
                "         \"remark\": \"品类限制：成人用品\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5260\",\n" +
                "         \"remark\": \"品类限制：成人用品\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5209\",\n" +
                "         \"remark\": \"包裹丢失\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5216\",\n" +
                "         \"remark\": \"包裹丢失\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5208\",\n" +
                "         \"remark\": \"包裹破损\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5215\",\n" +
                "         \"remark\": \"包裹破损\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5251\",\n" +
                "         \"remark\": \"品类限制:其他\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5256\",\n" +
                "         \"remark\": \"品类限制:其他\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5217\",\n" +
                "         \"remark\": \"包裹超轻\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5213\",\n" +
                "         \"remark\": \"包裹超体积\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5211\",\n" +
                "         \"remark\": \"包裹超重\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5247\",\n" +
                "         \"remark\": \"包裹信息不全\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5245\",\n" +
                "         \"remark\": \"标签问题\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5282\",\n" +
                "         \"remark\": \"包装不合格\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5278\",\n" +
                "         \"remark\": \"异型件\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5274\",\n" +
                "         \"remark\": \"品类限制：易碎品\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5285\",\n" +
                "         \"remark\": \"商业发票等资料缺失\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5286\",\n" +
                "         \"remark\": \"申报品名不详\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5284\",\n" +
                "         \"remark\": \"收件人城市、邮编不符\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5283\",\n" +
                "         \"remark\": \"收件人姓名电话不完整\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5453\",\n" +
                "         \"remark\": \"安检限制：违禁品\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5454\",\n" +
                "         \"remark\": \"安检限制：限运品-带电\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5454\",\n" +
                "         \"remark\": \"安检限制：限运品-带电\",\n" +
                "         \"nodeCode\": \"TRANSIT\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5455\",\n" +
                "         \"remark\": \"安检限制：限运品-特货（含磁商品）\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5456\",\n" +
                "         \"remark\": \"安检限制：限运品-特货（液体/膏状/粉末等）\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5478\",\n" +
                "         \"remark\": \"海关限制：疑似仿牌、IPR\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5479\",\n" +
                "         \"remark\": \"海关限制：无法出口（成人用品、医疗用品、种子、药品等）\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5457\",\n" +
                "         \"remark\": \"包裹破损\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5480\",\n" +
                "         \"remark\": \"信息问题：品名不符、申报不符\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5481\",\n" +
                "         \"remark\": \"信息问题：收件人地址、邮编、电话、姓名有误\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5482\",\n" +
                "         \"remark\": \"信息问题：重量、体积差异\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5483\",\n" +
                "         \"remark\": \"包裹丢失\",\n" +
                "         \"nodeCode\": \"LINEHAUL\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5829\",\n" +
                "         \"remark\": \"品类限制：带电\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5830\",\n" +
                "         \"remark\": \"品类限制：电容类\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5831\",\n" +
                "         \"remark\": \"品类限制：液体\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5832\",\n" +
                "         \"remark\": \"品类限制：粉末\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5833\",\n" +
                "         \"remark\": \"品类限制：膏状体\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5834\",\n" +
                "         \"remark\": \"品类限制：易碎品\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5835\",\n" +
                "         \"remark\": \"品类限制：磁性物品\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5836\",\n" +
                "         \"remark\": \"限运品\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5837\",\n" +
                "         \"remark\": \"禁运品：枪支弹药\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5838\",\n" +
                "         \"remark\": \"禁运品：管制器具\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5839\",\n" +
                "         \"remark\": \"禁运品：易燃易爆\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5840\",\n" +
                "         \"remark\": \"禁运品：压缩容器（含打火机、电子烟等）\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5841\",\n" +
                "         \"remark\": \"禁运品：毒性物质（毒品、食品、药品、活体）\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5842\",\n" +
                "         \"remark\": \"禁运品\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5843\",\n" +
                "         \"remark\": \"海外退\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5284\",\n" +
                "         \"remark\": \"破损\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5285\",\n" +
                "         \"remark\": \"丢失\",\n" +
                "         \"nodeCode\": \"CONSO\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5245\",\n" +
                "         \"remark\": \"标签问题\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5247\",\n" +
                "         \"remark\": \"包裹信息不全\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5251\",\n" +
                "         \"remark\": \"品类限制:其他\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5211\",\n" +
                "         \"remark\": \"包裹超重\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5217\",\n" +
                "         \"remark\": \"包裹超轻\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5213\",\n" +
                "         \"remark\": \"包裹超体积\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5225\",\n" +
                "         \"remark\": \"IPR问题\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5243\",\n" +
                "         \"remark\": \"品类限制：纯电\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5208\",\n" +
                "         \"remark\": \"包裹破损\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5264\",\n" +
                "         \"remark\": \"申报金额问题\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5242\",\n" +
                "         \"remark\": \"品类限制：带电\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5269\",\n" +
                "         \"remark\": \"品类限制：化工品类\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5270\",\n" +
                "         \"remark\": \"品类限制：电容类\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5271\",\n" +
                "         \"remark\": \"品类限制：液体\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5272\",\n" +
                "         \"remark\": \"品类限制：粉末\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5273\",\n" +
                "         \"remark\": \"品类限制：膏状体\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5274\",\n" +
                "         \"remark\": \"品类限制：易碎品\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5275\",\n" +
                "         \"remark\": \"品类限制：磁性物品\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5276\",\n" +
                "         \"remark\": \"禁运品：枪支弹药\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5277\",\n" +
                "         \"remark\": \"禁运品：管制器具\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5291\",\n" +
                "         \"remark\": \"禁运品：易燃易爆\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5279\",\n" +
                "         \"remark\": \"禁运品：压缩容器（含打火机、电子烟等）\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5280\",\n" +
                "         \"remark\": \"禁运品：毒性物质（毒品、食品、药品、活体）\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5281\",\n" +
                "         \"remark\": \"禁运品：纸币、色子、公章、筹码等\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5282\",\n" +
                "         \"remark\": \"包装不合格\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5283\",\n" +
                "         \"remark\": \"收件人姓名电话不完整\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5284\",\n" +
                "         \"remark\": \"收件人城市、邮编不符\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5285\",\n" +
                "         \"remark\": \"商业发票等资料缺失\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5286\",\n" +
                "         \"remark\": \"申报品名不详\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5209\",\n" +
                "         \"remark\": \"包裹丢失\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5256\",\n" +
                "         \"remark\": \"品类限制:其他\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5215\",\n" +
                "         \"remark\": \"包裹破损\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5216\",\n" +
                "         \"remark\": \"包裹丢失\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5258\",\n" +
                "         \"remark\": \"品类限制：成人用品\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5278\",\n" +
                "         \"remark\": \"异型件\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5260\",\n" +
                "         \"remark\": \"品类限制：成人用品\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5244\",\n" +
                "         \"remark\": \"品类限制：化妆品\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5405\",\n" +
                "         \"remark\": \"包裹包装不符合产品要求\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5406\",\n" +
                "         \"remark\": \"产品类目限制，非服装\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5404\",\n" +
                "         \"remark\": \"普货产品含带电货物\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5403\",\n" +
                "         \"remark\": \"账户已停止服务\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5407\",\n" +
                "         \"remark\": \"异形件\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5401\",\n" +
                "         \"remark\": \"体积重大于实重，需要按照体积重计费\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"opCode\": \"5402\",\n" +
                "         \"remark\": \"预付款账户已欠费\",\n" +
                "         \"nodeCode\": \"SORTING_CENTER\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"PRINT_WAYBILL_PRE_CHECK\": true,\n" +
                "   \"PROCESS_LH_PACKAGE_SC\": [\n" +
                "      \"Tran_Store_13423966\",\n" +
                "      \"TRAN_STORE_30547804\",\n" +
                "      \"TRAN_STORE_30526976\",\n" +
                "      \"Tran_Store_13423967\",\n" +
                "      \"Tran_Store_13423969\",\n" +
                "      \"Tran_Store_13452137\",\n" +
                "      \"TRAN_STORE_30299685\",\n" +
                "      \"TRAN_STORE_30299953\",\n" +
                "      \"Tran_Store_13452714\",\n" +
                "      \"TRAN_STORE_30320880\"\n" +
                "   ],\n" +
                "   \"INBOUND_FAIL_CREATE_WORKORDER_OPCODE_LIST\": [\n" +
                "      {\n" +
                "         \"opCode\": \"5905\",\n" +
                "         \"bizTypeId\": 1000023123,\n" +
                "         \"workOrderInfo\": \"包裹退件入库发现破损,物流商检查核实\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"CONSO_INBOUND_FAIL_OP_CODE_LIST\": [\n" +
                "      \"5938\",\n" +
                "      \"5905\",\n" +
                "      \"5906\",\n" +
                "      \"5939\",\n" +
                "      \"5940\",\n" +
                "      \"5941\",\n" +
                "      \"5942\"\n" +
                "   ],\n" +
                "   \"INVALID_CP_CODE_MAP\": {\n" +
                "      \"5\": [\n" +
                "         \"DISTRIBUTOR_30476027\"\n" +
                "      ],\n" +
                "      \"12\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"13\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"15\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"17\": [\n" +
                "         \"DISTRIBUTOR_30442375\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"21\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"23\": [\n" +
                "         \"DISTRIBUTOR_13331996\"\n" +
                "      ],\n" +
                "      \"33\": [\n" +
                "         \"DISTRIBUTOR_30442375\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"37\": [\n" +
                "         \"DISTRIBUTOR_30520836\"\n" +
                "      ],\n" +
                "      \"50\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"51\": [\n" +
                "         \"DISTRIBUTOR_30442375\",\n" +
                "         \"DISTRIBUTOR_31089597\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"53\": [\n" +
                "         \"DISTRIBUTOR_30476027\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"54\": [\n" +
                "         \"DISTRIBUTOR_30943423\",\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"56\": [\n" +
                "         \"DISTRIBUTOR_30776027\"\n" +
                "      ],\n" +
                "      \"66\": [\n" +
                "         \"DISTRIBUTOR_30455584\"\n" +
                "      ],\n" +
                "      \"71\": [\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_31059987\"\n" +
                "      ],\n" +
                "      \"80\": [\n" +
                "         \"DISTRIBUTOR_30939910\",\n" +
                "         \"DISTRIBUTOR_30476027\"\n" +
                "      ],\n" +
                "      \"89\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"92\": [\n" +
                "         \"DISTRIBUTOR_30442375\",\n" +
                "         \"DISTRIBUTOR_30476027\"\n" +
                "      ],\n" +
                "      \"93\": [\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"102\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"114\": [\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_30455584\"\n" +
                "      ],\n" +
                "      \"120\": [\n" +
                "         \"DISTRIBUTOR_13331996\",\n" +
                "         \"DISTRIBUTOR_30455584\"\n" +
                "      ],\n" +
                "      \"122\": [\n" +
                "         \"DISTRIBUTOR_30476027\"\n" +
                "      ],\n" +
                "      \"128\": [\n" +
                "         \"DISTRIBUTOR_30874723\"\n" +
                "      ],\n" +
                "      \"147\": [\n" +
                "         \"DISTRIBUTOR_13474562\"\n" +
                "      ],\n" +
                "      \"171\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"173\": [\n" +
                "         \"DISTRIBUTOR_30476027\",\n" +
                "         \"DISTRIBUTOR_30442375\"\n" +
                "      ],\n" +
                "      \"179\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"191\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"192\": [\n" +
                "         \"DISTRIBUTOR_30442375\",\n" +
                "         \"DISTRIBUTOR_31089597\"\n" +
                "      ],\n" +
                "      \"193\": [\n" +
                "         \"DISTRIBUTOR_30442375\",\n" +
                "         \"DISTRIBUTOR_30476027\"\n" +
                "      ],\n" +
                "      \"199\": [\n" +
                "         \"DISTRIBUTOR_31036065\"\n" +
                "      ],\n" +
                "      \"205\": [\n" +
                "         \"DISTRIBUTOR_30776027\"\n" +
                "      ],\n" +
                "      \"218\": [\n" +
                "         \"DISTRIBUTOR_30223716\",\n" +
                "         \"DISTRIBUTOR_30696167\"\n" +
                "      ],\n" +
                "      \"223\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ],\n" +
                "      \"225\": [\n" +
                "         \"DISTRIBUTOR_13473712\"\n" +
                "      ],\n" +
                "      \"229\": [\n" +
                "         \"DISTRIBUTOR_1216244\"\n" +
                "      ]\n" +
                "   },\n" +
                "   \"YTO_RETURN_SELLER_ID_MAP\": {\n" +
                "      \"POP\": 2787715648,\n" +
                "      \"CHOICE\": 1739253582L\n" +
                "   }\n" +
                "}";
        JSONObject context = JSON.parseObject(configInfo);
        System.out.println(context);
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


}


