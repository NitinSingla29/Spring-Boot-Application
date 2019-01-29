package com.demo.weatherservice.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@SuppressWarnings("serial")
public class SwaggerConfiguration {

	public static final Contact DEFAULT_CONTACT = new Contact("Nitin Singla", "", "ntinsingla@gmail.com");
	public static final ApiInfo DEFAULT_API_INFO = new ApiInfo("Weather Service", "Weather Service Documentation",
			"1.0", "", DEFAULT_CONTACT, "1.0.0-SNAPSHOT", "http://www.apache.org/licenses/LICENSE-2.0");
	private static final Set<String> DEFAULT_PRODUCE_AND_CONSUME = new HashSet<String>() {
		{
			this.add("application/json");
		}
	};

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(DEFAULT_API_INFO).produces(DEFAULT_PRODUCE_AND_CONSUME)
				.consumes(DEFAULT_PRODUCE_AND_CONSUME);
	}
}
