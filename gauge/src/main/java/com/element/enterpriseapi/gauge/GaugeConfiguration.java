package com.element.enterpriseapi.gauge;

import com.element.enterpriseapi.CommonConfiguration;
import com.element.enterpriseapi.EdbDatasourceConfiguration;
import com.element.enterpriseapi.MainframeDatasourceConfiguration;
import com.element.enterpriseapi.common.JdbcAssetIdConverter;
import com.element.enterpriseapi.common.JdbcOrgIdConverter;
import com.element.enterpriseapi.gauge.mileage.InsertMileageLambdaHandler;
import com.element.enterpriseapi.gauge.mileage.MileageInputMessageConverter;
import com.element.enterpriseapi.gauge.mileage.UpdateMileageLambdaHandler;
import com.element.enterpriseapi.gauge.odometer.InsertOdometerReadingLambdaHandler;
import com.element.enterpriseapi.gauge.odometer.OdometerInputMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(
        {
                CommonConfiguration.class,

                JdbcAssetIdConverter.class,
                JdbcOrgIdConverter.class,
                EdbDatasourceConfiguration.class,

                InsertMileageLambdaHandler.class,
                UpdateMileageLambdaHandler.class,
                MileageInputMessageConverter.class,

                InsertOdometerReadingLambdaHandler.class,
                OdometerInputMessageConverter.class
        }
)
public class GaugeConfiguration {

}
// Multiple DataSource config. See https://www.baeldung.com/spring-boot-configure-multiple-datasources
