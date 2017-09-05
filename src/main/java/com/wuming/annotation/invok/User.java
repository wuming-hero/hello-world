package com.wuming.annotation.invok;

/**
 * @author wuming
 * Created on 2017/9/5 12:00
 */
@TableSQL
public class User {

    //定义id字段，与表user的列id相映射，指定约束为：不为空，为主键。
    @TableColumnSQL(value = "id", constraint = @Constraint(allowNull = false, isPrimary = true))
    private Long id;

    //只为注解指定value字段，可省略value。
    @TableColumnSQL("user_name")
    private String name;

}
