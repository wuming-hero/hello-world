package com.wuming.base;

import com.wuming.model.Account;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;

/**
 * @author wuming
 * Created on 2018/5/5 11:34
 */
public class ParameterizedTypeTest {

    @Test
    public void test() {
        AccountDao accountDao = new AccountDao();
        System.out.println("nameSpace: " + accountDao.getNameSpace()); // nameSpace: Account
        System.out.println("entityClass: " + accountDao.getEntityClass()); // entityClass: class com.wuming.annotation.invok.Account
        Class accountClass = accountDao.getEntityClass();
        System.out.println(accountClass);
        System.out.println(accountClass.getSimpleName());
        System.out.println(accountClass.getName());
        System.out.println(accountClass.getTypeName());
        System.out.println(accountClass.getCanonicalName());
    }

}

/**
 * AccountDao继承MyBatisDao
 */
class AccountDao extends MyBatisDao<Account> {

    public AccountDao() {
        System.out.println("-nameSpace: " + this.getNameSpace()); // -nameSpace: Account
        System.out.println("-entityClass: " + this.getEntityClass()); // -entityClass: class com.wuming.annotation.invok.Account
    }

}

/**
 * 父类使用范型参数声明
 * <p>
 * getClass().getGenericSuperclass()
 * 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type，然后将其转换ParameterizedType。
 * <p>
 * getActualTypeArguments()
 * 返回表示此类型实际类型参数的 Type 对象的数组。[0]就是这个数组中第一个了，假如有多个通过不同的下标从数组中读取。简而言之就是获得超类的泛型参数的实际类型。。
 *
 * @param <T>
 */
class MyBatisDao<T> {

    private String nameSpace;
    private Class entityClass;

    public MyBatisDao() {
        if ((getClass().getGenericSuperclass() instanceof ParameterizedType)) {
            // 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type，然后将其转换ParameterizedType。
            ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
            System.out.println("----ParameterizedType: " + pt); // com.wuming.base.MyBatisDao<com.wuming.annotation.invok.Account>
            this.entityClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.nameSpace = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
            System.out.println("----nameSpace: " + this.nameSpace); // ----nameSpace: Account
        } else {
            this.entityClass = (Class) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.nameSpace = ((Class) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
            System.out.println("++++nameSpace: " + this.nameSpace);
        }
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

}
