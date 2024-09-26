package com.element.enterpriseapi.gauge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(GaugeConfiguration.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*"))
public class GaugeApplication {
    public static void main(String[] args) {
        SpringApplication.run(GaugeApplication.class, args);
    }
}
