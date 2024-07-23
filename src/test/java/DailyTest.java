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
    public void subListTest() {
        List<Integer> dataList = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println(dataList.subList(0, dataList.size()));
        String data = String.format("%s:%s", null, null);
        System.out.println(data);
//        System.out.println(NumberUtils.isCreatable("1.23111"));
//        System.out.println(NumberUtils.isCreatable("0.23"));
//        System.out.println(NumberUtils.isCreatable("0.23"));

        System.out.println(NumberUtils.isParsable("-.11"));
        System.out.println(NumberUtils.isParsable("1.23111"));
        System.out.println(NumberUtils.isParsable("-0.23"));
        System.out.println(NumberUtils.isParsable("0.23"));
        System.out.println(new BigDecimal("-.11").multiply(new BigDecimal(100)).longValue());

        List<String> lpCodeList = Lists.newArrayList(
                "LP00641363740503",
                "LP00642228397050",
                "LP00643106441744",
                "LP00643381953676",
                "LP00644450698594",
                "LP00645335515873",
                "LP00648412578553",
                "LP00645236996933",
                "LP00646178404057",
                "LP00642789270843",
                "LP00645604716604",
                "LP00646273354910",
                "LP00649763798833",
                "LP00644326726733",
                "LP00649123427886",
                "LP00650021180142",
                "LP00643186757427",
                "LP00645485567892",
                "LP00647029046297",
                "LP00649632345403",
                "LP00643881507249",
                "LP00644650846418",
                "LP00644880868558",
                "LP00645117349778",
                "LP00645179912164",
                "LP00645192095244",
                "LP00645264207857",
                "LP00645336587038",
                "LP00645805440036",
                "LP00646163294532",
                "LP00646352549531",
                "LP00646648296954",
                "LP00646872166091",
                "LP00648022047098",
                "LP00648248669785",
                "LP00648360772775",
                "LP00649018931234",
                "LP00649056155710",
                "LP00649147766901",
                "LP00649306267031",
                "LP00649717873525",
                "LP00650960127351",
                "LP00646272239890",
                "LP00649668456037",
                "LP00651620147071",
                "LP00651621154369",
                "LP00652710967454",
                "LP00652711758496",
                "LP00648041045823",
                "LP00648896756980",
                "LP00649099999409",
                "LP00649556893250",
                "LP00640481810665",
                "LP00649292913912",
                "LP00647568980027",
                "LP00648697914125",
                "LP00648922604220",
                "LP00649001012365",
                "LP00649483967255",
                "LP00649844235875",
                "LP00649983917727",
                "LP00650121856388",
                "LP00650173310705",
                "LP00650658663128",
                "LP00650663706568",
                "LP00651014220164",
                "LP00642110048630",
                "LP00649396374268",
                "LP00650176872599",
                "LP00651822235413",
                "LP00645008610349",
                "LP00646213741053",
                "LP00646274170276",
                "LP00646392209546",
                "LP00646697119597",
                "LP00646842587882",
                "LP00647259257250",
                "LP00647765846645",
                "LP00648292184626",
                "LP00648939194684",
                "LP00650258884739",
                "LP00650264746637",
                "LP00650632907930",
                "LP00647005691639",
                "LP00651690643241",
                "LP00652809760428",
                "LP00654174499524",
                "LP00654380424419",
                "LP00654623934158",
                "LP00654951734180",
                "LP00655677109878",
                "LP00647192692299",
                "LP00647201408013",
                "LP00648116933011",
                "LP00648280944956",
                "LP00648640084476",
                "LP00648706038449",
                "LP00649572218718",
                "LP00650046662818",
                "LP00650701215799",
                "LP00653256711203",
                "LP00653270339949",
                "LP00653527293294",
                "LP00646724379834",
                "LP00647665750661",
                "LP00647689084215",
                "LP00648182786073",
                "LP00648625204069",
                "LP00648727262795",
                "LP00649851695073",
                "LP00650958035299",
                "LP00651771834002",
                "LP00652325467752",
                "LP00655052502520",
                "LP00655405144201",
                "LP00649582400696",
                "LP00648916694815",
                "LP00651469918858",
                "LP00652709214816",
                "LP00655078408009",
                "LP00655623632233",
                "LP00657200191063",
                "LP00655984303873",
                "LP00653632992219",
                "LP00653641729170",
                "LP00655342277925",
                "LP00655730061831",
                "LP00656464255727",
                "LP00656543011356",
                "LP00657073656363",
                "LP00653935203820",
                "LP00654403525395",
                "LP00654425244364",
                "LP00647164228413",
                "LP00652068222035",
                "LP00653054512536",
                "LP00655350855161",
                "LP00648463291949",
                "LP00649055321386",
                "LP00649813166086",
                "LP00650125956389",
                "LP00651209108926",
                "LP00653245475323",
                "LP00653267884554",
                "LP00653413174191",
                "LP00653844584466",
                "LP00653862926159",
                "LP00654270854251",
                "LP00654532537951",
                "LP00654806014821",
                "LP00655383542680",
                "LP00655577199076",
                "LP00655938979023",
                "LP00657454322137",
                "LP00657854992025",
                "LP00658494779882",
                "LP00647616269146",
                "LP00648352301962",
                "LP00648803266108",
                "LP00649098832475",
                "LP00649720901687",
                "LP00649726624931",
                "LP00649884630277",
                "LP00649950457287",
                "LP00650049602948",
                "LP00650064782624",
                "LP00650260433400",
                "LP00650264175048",
                "LP00650354708201",
                "LP00650597554458",
                "LP00650609909636",
                "LP00651064273716",
                "LP00651266629230",
                "LP00652078898174",
                "LP00652115992140",
                "LP00652325893693",
                "LP00652365980341",
                "LP00652614122486",
                "LP00652848037618",
                "LP00652876320664",
                "LP00653561338450",
                "LP00653949910699",
                "LP00654874094158",
                "LP00655293003709",
                "LP00655362554723",
                "LP00655374477835",
                "LP00655515458005",
                "LP00655710465822",
                "LP00655766952598",
                "LP00655941546085",
                "LP00656028307114",
                "LP00656457758954",
                "LP00656637264524",
                "LP00657047946159",
                "LP00655981476838",
                "LP00656959167837",
                "LP00657760021317",
                "LP00658138121569",
                "LP00656184921102",
                "LP00657048561990",
                "LP00658458555688",
                "LP00658754413315",
                "LP00658861046885",
                "LP00659051360921",
                "LP00659053070477",
                "LP00659056573735",
                "LP00659590381380",
                "LP00659625786633",
                "LP00653624203060",
                "LP00656705211413",
                "LP00659856582391",
                "LP00653152409726",
                "LP00656464049383",
                "LP00657892190706",
                "LP00658024547398",
                "LP00658147173293",
                "LP00658847955988",
                "LP00652347418172",
                "LP00652439282927",
                "LP00652444430728",
                "LP00652543427834",
                "LP00652988718255",
                "LP00653906772944",
                "LP00654001507023",
                "LP00657276637910",
                "LP00657526873545",
                "LP00659298638129",
                "LP00659567040399",
                "LP00654542735809",
                "LP00654624919815",
                "LP00656632140138",
                "LP00653097507549",
                "LP00659337830732",
                "LP00650427108570",
                "LP00652646020837",
                "LP00653387256927",
                "LP00654124419218",
                "LP00654143865460",
                "LP00654158723446",
                "LP00654503786501",
                "LP00654797801296",
                "LP00655179082252",
                "LP00655387857485",
                "LP00655675317496",
                "LP00656214126034",
                "LP00656416309483",
                "LP00656487480824",
                "LP00656543845432",
                "LP00656660791296",
                "LP00656747612580",
                "LP00657315327993",
                "LP00657494619477",
                "LP00657638639559",
                "LP00657643619924",
                "LP00657710765188",
                "LP00658399055387",
                "LP00658540296190",
                "LP00658802221384",
                "LP00653664081791",
                "LP00655396978268",
                "LP00655844918370",
                "LP00655858113755",
                "LP00656261143253",
                "LP00657521959084",
                "LP00659151877843",
                "LP00661001322927");

        String sqlTmpl = "UPDATE `cndcp_tms_order` SET `is_deleted` = 'Y', `gmt_modified` = now() WHERE `order_type` = 1 AND `lg_order_code` = '${lpCode}';\n" +
                "DELETE FROM  `cndcp_repeat_check` WHERE `biz_type` = 'createNewTms' AND `unique_id` LIKE '%#5000000011405#%' AND `lg_order_code` = '${lpCode}';\n" +
                "UPDATE `global_fulfill_order` SET `is_deleted` = -1, `gmt_modified` = now() WHERE `order_type` = 1 AND `lg_order_code` = '${lpCode}';\n" +
                "DELETE FROM `global_repeat_check` WHERE `unique_key` LIKE '%TRANSIT_UNREACHABLE_RETURN_CALLBACK%' AND `lg_order_code` = '${lpCode}';";

        for (String lpCode : lpCodeList) {
            System.out.println(sqlTmpl.replace("${lpCode}", lpCode));
            System.out.println("\n");
        }
    }

}
