package com.zenika.enigma.chronobidule.central;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class ApplicationTests {

	@Container
	private static PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:17.4-alpine");

	@Test
	void contextLoads() {
	}

	@DynamicPropertySource
	static void dynamicDatasourceProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", DB_CONTAINER::getUsername);
		registry.add("spring.datasource.password", DB_CONTAINER::getPassword);
	}

}
