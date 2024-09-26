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
public class MainframeDatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.mainframe")
    public DataSourceProperties mainframeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Lazy
    @Bean
    @ConfigurationProperties("spring.datasource.mainframe.hikari")
    public DataSource mainframeDataSource() {
        return mainframeDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean("mainframeNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate mainframeNamedParameterJdbcTemplate() {
        DataSource dataSource = mainframeDataSource();
        return new NamedParameterJdbcTemplate(dataSource);
    }


    @Bean("mainframeTxManager")
    public PlatformTransactionManager mainframeTransactionManager() {
        DataSource dataSource = mainframeDataSource();
        return new DataSourceTransactionManager(dataSource);
    }


}
