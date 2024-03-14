package com.banksys.accountsys.configuration.swagger2Config;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/1/19 9:14
 * 访问地址：localhost:8080/swagger-ui.html
 */
// @Configuration
//启用swagger2
// @EnableSwagger2
public class Swagger2 {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描路径
                .apis(RequestHandlerSelectors.basePackage("com.banksys.banksysback.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("标题")
                .description("描述")
                .termsOfServiceUrl("服务条款网址https://www.baidu.com")
                .version("版本v1.0")
                .build();
    }
}
