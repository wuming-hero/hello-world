import com.wuming.model.Son;
import com.wuming.util.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author wuming
 * Created on 2017/9/11 20:51
 */
public class ReflectionUtilsTest {

    /**
     * 获取方法
      */
    @Test
    public void testGetDeclaredMethod() {
        Object obj = new Son();
        //获取公共方法名
        Method publicMethod = ReflectionUtils.getDeclaredMethod(obj, "publicMethod");
        System.out.println(publicMethod.getName());

        //获取默认方法名
        Method defaultMethod = ReflectionUtils.getDeclaredMethod(obj, "defaultMethod");
        System.out.println(defaultMethod.getName());

        //获取被保护方法名
        Method protectedMethod = ReflectionUtils.getDeclaredMethod(obj, "protectedMethod");
        System.out.println(protectedMethod.getName());

        //获取私有方法名
        Method privateMethod = ReflectionUtils.getDeclaredMethod(obj, "privateMethod");
        System.out.println(privateMethod.getName());
    }

    /**
     * 测试调用父类的方法
     *
     * @throws Exception
     */
    @Test
    public void testInvokeMethod() throws Exception {
        Object obj = new Son();

        //调用父类的公共方法
        ReflectionUtils.invokeMethod(obj, "publicMethod", null, null);

        //调用父类的默认方法
        ReflectionUtils.invokeMethod(obj, "defaultMethod", null, null);

        //调用父类的被保护方法
        ReflectionUtils.invokeMethod(obj, "protectedMethod", null, null);

        //调用父类的私有方法
        ReflectionUtils.invokeMethod(obj, "privateMethod", null, null);
    }

    /**
     * 测试获取父类的各个属性名
     */
    @Test
    public void testGetDeclaredField() {
        Object obj = new Son();
        //获取公共属性名
        Field publicField = ReflectionUtils.getDeclaredField(obj, "publicField");
        System.out.println(publicField.getName());

        //获取公共属性名
        Field defaultField = ReflectionUtils.getDeclaredField(obj, "defaultField");
        System.out.println(defaultField.getName());

        //获取公共属性名
        Field protectedField = ReflectionUtils.getDeclaredField(obj, "protectedField");
        System.out.println(protectedField.getName());

        //获取公共属性名
        Field privateField = ReflectionUtils.getDeclaredField(obj, "privateField");
        System.out.println(privateField.getName());

    }

    /**
     * 获取类成员变量值和修改他们的值
     */
    @Test
    public void testSetFieldValue() {
        Object obj = new Son();
        System.out.println("原来的各个属性的值: ");
        System.out.println("publicField = " + ReflectionUtils.getFieldValue(obj, "publicField"));
        System.out.println("defaultField = " + ReflectionUtils.getFieldValue(obj, "defaultField"));
        System.out.println("protectedField = " + ReflectionUtils.getFieldValue(obj, "protectedField"));
        System.out.println("privateField = " + ReflectionUtils.getFieldValue(obj, "privateField"));

        ReflectionUtils.setFieldValue(obj, "publicField", "a");
        ReflectionUtils.setFieldValue(obj, "defaultField", "b");
        ReflectionUtils.setFieldValue(obj, "protectedField", "c");
        ReflectionUtils.setFieldValue(obj, "privateField", "d");

        System.out.println("***********************************************************");

        System.out.println("将属性值改变后的各个属性值: ");
        System.out.println("publicField = " + ReflectionUtils.getFieldValue(obj, "publicField"));
        System.out.println("defaultField = " + ReflectionUtils.getFieldValue(obj, "defaultField"));
        System.out.println("protectedField = " + ReflectionUtils.getFieldValue(obj, "protectedField"));
        System.out.println("privateField = " + ReflectionUtils.getFieldValue(obj, "privateField"));

    }

    @Test
    public void testGetFieldValue() {

        Object obj = new Son();

        System.out.println("publicField = " + ReflectionUtils.getFieldValue(obj, "publicField"));
        System.out.println("defaultField = " + ReflectionUtils.getFieldValue(obj, "defaultField"));
        System.out.println("protectedField = " + ReflectionUtils.getFieldValue(obj, "protectedField"));
        System.out.println("privateField = " + ReflectionUtils.getFieldValue(obj, "privateField"));
    }
}
