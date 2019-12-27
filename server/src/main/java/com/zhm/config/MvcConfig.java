package com.zhm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by 赵红明 on 2019/6/14.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    private static Logger logger= LoggerFactory.getLogger(MvcConfig.class);
    @Autowired
    private Environment environment;

    /**
     * 分页设置
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
        sortArgumentResolver.setPropertyDelimiter(",");
        argumentResolvers.add(sortArgumentResolver);
        PageableHandlerMethodArgumentResolver pageableArgumentResolver = new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
        pageableArgumentResolver.setPageParameterName("page");
        pageableArgumentResolver.setSizeParameterName("size");
        pageableArgumentResolver.setOneIndexedParameters(true);
        argumentResolvers.add(pageableArgumentResolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    /**
     *图片等地址映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("pdfURL="+environment.getProperty("service.addrss"));
        registry.addResourceHandler("/pdf/**").addResourceLocations("file:"+environment.getProperty("service.addrss"));
        super.addResourceHandlers(registry);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setMaxPayloadLength(2048);
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(true);
        return loggingFilter;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
