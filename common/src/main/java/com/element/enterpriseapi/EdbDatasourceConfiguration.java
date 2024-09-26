package com.element.enterpriseapi;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// Multiple DataSource config. See https://www.baeldung.com/spring-boot-configure-multiple-datasources
@Configuration
public class EdbDatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.edb")
    public DataSourceProperties edbDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Lazy
    @Bean
    @ConfigurationProperties("spring.datasource.edb.hikari")
    public DataSource edbDataSource() {
        return edbDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean("edbNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate edbNamedParameterJdbcTemplate() {
        DataSource dataSource = edbDataSource();
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean("edbTxManager")
    public PlatformTransactionManager edbTransactionManager() {
        DataSource dataSource = edbDataSource();
        return new DataSourceTransactionManager(dataSource);
    }
}
