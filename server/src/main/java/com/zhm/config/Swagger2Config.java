package com.zhm.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 查看接口Swagger-ui设置
 * Created by 赵红明 on 2019/5/27.
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Autowired
    private Environment environment;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(selector())
                .paths(PathSelectors.any())
                .build();
    }

    private Predicate<RequestHandler> selector() {
//        return RequestHandlerSelectors.withMethodAnnotation(RequestMapping.class);
        return RequestHandlerSelectors.basePackage("com.zhm.controller");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                .description(environment.getProperty("spring.application.name", "spring boot application"))
                .termsOfServiceUrl("http://www.97kankan.xin")
                .contact(new Contact("Json.zhao", "http://www.97kankan.xin", "1183483051@qq.com"))
                .version(environment.getProperty("spring.application.version", "1.0.0"))
                .build();
    }
}
