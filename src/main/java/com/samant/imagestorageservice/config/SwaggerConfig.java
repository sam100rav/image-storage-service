package com.samant.imagestorageservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.samant.imagestorageservice.resource"))
				.paths(PathSelectors.any())
				.build().apiInfo(metaData())
				.useDefaultResponseMessages(false);
	}
	
	private ApiInfo metaData() {
		return new ApiInfoBuilder()
				.title("Image Storage Service using Spring Boot Micro-service")
				.description("A microservice to create, fetch and delete images and albums, "
						+ "using Spring Boot Web, Spring Boot Data JPA, Swagger-ui & Docker")
				.version("1.0")
				.contact(new Contact("Saurav Samant", "https://linkedin.com/in/saurav-samant-80623265/", 
						"samantsinha@hotmail.com"))
				.build();
	}

}
