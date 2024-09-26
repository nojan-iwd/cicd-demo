package com.element.enterpriseapi;

import com.element.enterpriseapi.common.CustomMessageRouter;
import com.element.enterpriseapi.common.FQN;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.instrumentation.spring.autoconfigure.EnableOpenTelemetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableOpenTelemetry
public class CommonConfiguration {

    @Bean
    public FQN fqn(@Value("${eapi.db.useDbo:true}") boolean useDbo) {
        return new FQN(useDbo);
    }

    @Bean
    public CustomMessageRouter customMessageRouter(ObjectMapper objectMapper) {
        return new CustomMessageRouter(objectMapper);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        return new DataSourceInitializer();
    }

}

