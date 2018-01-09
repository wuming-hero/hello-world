package com.wuming.annotation.invok;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuming
 * Created on 2017/9/5 12:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableColumnSQL {
    String value() default "";

    Constraint constraint() default @Constraint();
}
