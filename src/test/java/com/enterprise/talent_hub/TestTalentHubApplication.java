package com.enterprise.talent_hub;

import org.springframework.boot.SpringApplication;

public class TestTalentHubApplication {

	public static void main(String[] args) {
		SpringApplication.from(TalentHubApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
