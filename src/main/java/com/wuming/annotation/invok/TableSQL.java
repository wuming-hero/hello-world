package com.wuming.annotation.invok;

import java.lang.annotation.*;

/**
 * @author wuming
 * Created on 2017/9/5 12:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableSQL {
    String value() default "";
}
