package com.zhm.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.util.Map;

/**
 * Created by 赵红明 on 2019/6/17.
 */
public class SpringContextUtil {
    private static ApplicationContext getContext() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    public static <T> T getBean(String name) throws BeansException {
        ApplicationContext context = getContext();
        return (T) context.getBean(name);
    }

    public static <T> T getBean(Class<T> cls) {
        ApplicationContext context = getContext();
        return getBean(cls, context);
    }

    public static <T> T getBean(Class<T> cls, ApplicationContext ctx) {
        Map<String, T> map = ctx.getBeansOfType(cls);
        if (map.size() == 0) {
            return null;
        } else if (map.size() > 1) {
            new Exception("bean is not unique.").printStackTrace();
        }
        return map.values().iterator().next();
    }
}
