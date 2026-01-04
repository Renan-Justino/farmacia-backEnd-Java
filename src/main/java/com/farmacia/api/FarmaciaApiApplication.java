package com.farmacia.api;

import com.farmacia.api.infra.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class FarmaciaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmaciaApiApplication.class, args);
	}
}
