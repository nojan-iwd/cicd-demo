package com.element.enterpriseapi;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = ElementPostgresContainer.getInstance();

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registerDatasourceProperties(registry, "edb");
        registerDatasourceProperties(registry, "mainframe");
    }

    private static void registerDatasourceProperties(DynamicPropertyRegistry registry, String datasourceName) {
        System.out.println("Registering datasource: " + datasourceName + "; " + postgresqlContainer.getJdbcUrl());
        registry.add("spring.datasource.%s.driverClassName".formatted(datasourceName), postgresqlContainer::getDriverClassName);
        registry.add("spring.datasource.%s.url".formatted(datasourceName), postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.%s.username".formatted(datasourceName), postgresqlContainer::getUsername);
        registry.add("spring.datasource.%s.password".formatted(datasourceName), postgresqlContainer::getPassword);
    }

}
