package com.ecommerce.project.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ecommerce.project.backend.repository")  // ✅ 레포지토리 스캔
@EntityScan(basePackages = "com.ecommerce.project.backend.domain")                 // ✅ 엔티티 스캔
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
