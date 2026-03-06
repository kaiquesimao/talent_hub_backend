package com.enterprise.talent_hub.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ConfigurationProperties(prefix = "app.security")
@Validated
public class AppSecurityProperties {

	@Valid
	private Demo demo = new Demo();

	@Valid
	private Jwt jwt = new Jwt();

	public Demo getDemo() {
		return demo;
	}

	public void setDemo(Demo demo) {
		this.demo = demo;
	}

	public Jwt getJwt() {
		return jwt;
	}

	public void setJwt(Jwt jwt) {
		this.jwt = jwt;
	}

	@Validated
	public static class Demo {

		@NotBlank
		@Email
		private String email;

		@NotBlank
		@Size(min = 8, max = 120)
		private String password;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	@Validated
	public static class Jwt {

		@NotBlank
		@Size(min = 32)
		private String secret = "talent-hub-local-dev-secret-key-with-32chars";

		@NotNull
		private Duration ttl = Duration.ofHours(8);

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public Duration getTtl() {
			return ttl;
		}

		public void setTtl(Duration ttl) {
			this.ttl = ttl;
		}
	}
}
