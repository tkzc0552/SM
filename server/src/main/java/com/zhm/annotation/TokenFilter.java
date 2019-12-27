package com.zhm.annotation;



import java.lang.annotation.*;

/**
 * 忽略JWT token
 * Created by 赵红明 on 2019/9/11.
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenFilter {

}
