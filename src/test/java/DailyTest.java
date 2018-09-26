import com.wuming.model.Account;
import com.wuming.util.OrderIdUtil;
import com.wuming.util.OrderNoUtil;
import com.wuming.util.Sequence;
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

}
