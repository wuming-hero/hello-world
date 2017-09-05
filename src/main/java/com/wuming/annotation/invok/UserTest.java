package com.wuming.annotation.invok;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 通过反映机制，获取类及其属性上的注解，实现将一个mapper类转换为建表语句，
 * 实现中一些细节做了舍弃，在一个特定环境下进行实现
 * <p>
 * 假设User中 id为主键 name 为String类型
 *
 * @author wuming
 * Created on 2017/9/5 13:32
 */
public class UserTest {

    /**
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        Class clazz = User.class;
        // 获取User.class上的所有注解
        Annotation[] annotations = clazz.getAnnotations();
        annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println("annotation:" + annotation.toString());
        }

        // 判断clazz上是否有TableSQL注解
        if (clazz.isAnnotationPresent(TableSQL.class)) {
            StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE ");
            TableSQL tableSQL = (TableSQL) clazz.getAnnotation(TableSQL.class);
            // table name
            String tableName = tableSQL.value();
            //如果获取的值为TableSQL的默认值，则使用类名来做为表名
            if ("".equals(tableName)) {
                tableName = clazz.getSimpleName().toLowerCase();
            }
            sqlBuilder.append(tableName);
            sqlBuilder.append(" (\n");
            System.out.println("table name: " + tableName);
            Field[] fields = clazz.getDeclaredFields();
            String primaryKey = null;
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                System.out.println("field: " + field.getName());
                if (field.isAnnotationPresent(TableColumnSQL.class)) {
                    TableColumnSQL tableColumnSQL = field.getAnnotation(TableColumnSQL.class);
                    String fieldName = tableColumnSQL.value(); // 类成员变量映射的表中的字符名
                    Constraint constraint = tableColumnSQL.constraint();
                    boolean isPrimary = constraint.isPrimary();
                    boolean allowNull = constraint.allowNull();
                    sqlBuilder.append("\t" + fieldName);
                    if (isPrimary) {
                        if (Objects.nonNull(primaryKey)) {
                            throw new Exception("只能有一个主键！");
                        }
                        primaryKey = field.getName();
                        // 主键不能为空
                        sqlBuilder.append(" BIGINT(20) NOT NULL AUTO_INCREMENT");
                    } else if (!allowNull) {
                        // 如果不为空的话，需要处理，默认是可以为空的
                        sqlBuilder.append(" VARCHAR(32) NOT NULL");
                    } else {
                        sqlBuilder.append(" VARCHAR(32)");
                    }
                    sqlBuilder.append(",\n");
                }
                // 默认所有的元素都是
                if (i == fields.length - 1 && Objects.nonNull(primaryKey)) {
                    sqlBuilder.append("\tPRIMARY KEY (" + primaryKey + ")\n");
                    sqlBuilder.append(") DEFAULT CHARSET=utf8");
                }
            }
            // 打印SQL
            System.out.printf("create table: " + sqlBuilder.toString());
        }

    }

}
