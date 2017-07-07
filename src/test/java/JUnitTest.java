import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wuming on 16/6/26.
 * Junit中断言的使用
 * update dev
 */
public class JUnitTest {

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

}