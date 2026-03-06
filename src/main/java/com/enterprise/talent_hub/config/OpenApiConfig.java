package com.enterprise.talent_hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI talentHubOpenApi() {
		return new OpenAPI().info(new Info()
			.title("TalentHub API")
			.description("Internal mobility search API for TalentHub Global")
			.version("v1")
		);
	}
}
