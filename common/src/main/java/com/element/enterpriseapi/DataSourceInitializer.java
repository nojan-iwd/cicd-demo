package com.element.enterpriseapi;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceInitializer implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource dataSource) {
            try (Connection connection = dataSource.getConnection()) {
                // this will initialize the connection pool on startup
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to initialize the connection pool");
            }
        }
        return bean;
    }
}
