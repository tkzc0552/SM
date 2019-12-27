package com.zhm.annotation;

import java.lang.annotation.*;

/**
 * 系統日志注解
 * Created by 赵红明 on 2019/9/11.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogEntity {
    String value() default "";
}
