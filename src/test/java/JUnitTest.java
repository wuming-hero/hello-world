import com.google.common.collect.ImmutableList;
import com.wuming.enums.CalculateTypeEnum;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wuming on 16/6/26.
 * Junit中断言的使用
 * update dev
 */
public class JUnitTest {

    public static final char A = '：';
    private static final char[] CHARS = {':', '：', ' ', '\n', '\r'};

    @Test
    public void add() throws Exception {
        double result = 3;
        Assert.assertEquals("加法有问题", 3, result, 0);
    }

    @Test
    public void minus() throws Exception {
        double result = 1;
        Assert.assertEquals("减法有问题", 1, result, 0);
    }

    /**
     * 使用expected 抛出异常
     *
     * @throws Exception
     */
    @Test(expected = ArithmeticException.class)
    public void divide() throws Exception {
        double result = 1;
        Assert.assertEquals("除法有问题", 1, result, 0);
    }

    /**
     * 使用timeout 测试方法运行时间
     *
     * @throws Exception
     */
    @Test(timeout = 100)
    public void mul() throws Exception {
        double result = 14;
        Thread.sleep(150);
        Assert.assertEquals("乘法计算有误", 14, result, 0);
    }

    @Test
    public void StringTest() {
        String st1 = "a" + "b" + "c";
        String st2 = "abc";
        System.out.println(st1 == st2);
        System.out.println(st1.equals(st2));
    }

    @Test
    public void StringTest2() {
        String st1 = "ab" ;
        String st3 = st1 + "c";
        String st2 = "abc";
        String st4 = "ab" + "c";
        System.out.println(st3 == st2);
        System.out.println(st3.equals(st2));

        System.out.println(st4 == st2);
        System.out.println(st4.equals(st2));
    }


    @Test
    public void local() {
        List<Integer> dataList = ImmutableList.of(0, 1, 10, 100, 1000);
        System.out.println(dataList.stream().map(data -> data / 100).collect(Collectors.toList()));

        System.out.println(dataList.stream().map(data -> centsToYuan(Long.valueOf(data))).collect(Collectors.toList()));

        System.out.println(CalculateTypeEnum.VOLUME);
        System.out.println(String.valueOf(CalculateTypeEnum.VOLUME));
        System.out.println(String.valueOf(CalculateTypeEnum.VOLUME.name()));

    }

    public static String centsToYuan(Long cents) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(new BigDecimal(cents).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).doubleValue());
    }


    @Test
    public void dateTimetest() {
        String startTime = "2020-08-01";
        String endTime = "2020-08-26";
        LocalDate startDate = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int days = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        System.out.println(days);
        // 构建时间x轴, 日期最大一个月
        List<String> dateList = new ArrayList<>(days);
        Map<String, Integer> pvMap = new LinkedHashMap<>(days);
        Map<String, Integer> uvMap = new LinkedHashMap<>(days);
        Map<String, Integer> participateMap = new LinkedHashMap<>(days);
        Map<String, Integer> shareAmountMap = new LinkedHashMap<>(days);
        Map<String, Integer> shareTimesMap = new LinkedHashMap<>(days);
        for (int i = 0; i < days; i++) {
            LocalDate nowDate = startDate.plusDays(i);
            if (nowDate.isAfter(endDate)) {
                break;
            }
            String dateKey = nowDate.format(DateTimeFormatter.ofPattern("dd"));
            dateList.add(dateKey);
            pvMap.put(dateKey, 0);
            uvMap.put(dateKey, 0);
            participateMap.put(dateKey, 0);
            shareAmountMap.put(dateKey, 0);
            shareTimesMap.put(dateKey, 0);
        }
        System.out.println(dateList);
        System.out.println(pvMap);
    }

}