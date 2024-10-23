package com.golf.chat.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
// swagger lib 의존성 추가
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors; // springfox-core-2.2.2.jar
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType; // springfox-spi-2.0.2.jar
import springfox.documentation.spring.web.plugins.Docket; // springfox-spring-web-2.0.1.jar
import springfox.documentation.swagger2.annotations.EnableSwagger2; // springfox-swagger2-2.2.2.jar
// 그외 Predicate class 사용을 위해 guava jar를 라이브러리에 추가해야 된다. guava-22.0.jar

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("ICON KAKAO CHATBOT API")
                .description("KAKAO CHATBOT->ICON API 입니다.")
                .build();
    }

    @Bean
    public Docket api() {
    	return new Docket(DocumentationType.SWAGGER_2)
                .groupName("kakaoChatBot")
                .apiInfo(this.apiInfo())
                .select()
                //.apis(RequestHandlerSelectors.basePackage("com.golf.swagger.chat.controller"))
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/kakao/**"))
                .build();

    }
}
