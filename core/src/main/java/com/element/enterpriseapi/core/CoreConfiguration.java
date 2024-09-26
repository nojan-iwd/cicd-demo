package com.element.enterpriseapi.core;

import com.element.enterpriseapi.CommonConfiguration;
import com.element.enterpriseapi.EdbDatasourceConfiguration;
import com.element.enterpriseapi.MainframeDatasourceConfiguration;
import com.element.enterpriseapi.common.DbFieldGroupAspect;
import com.element.enterpriseapi.common.JdbcDealerAssignmentNumberConverter;
import com.element.enterpriseapi.common.JdbcOrgIdConverter;
import com.element.enterpriseapi.core.asset.UpdateAssetInputMessageConverter;
import com.element.enterpriseapi.core.asset.UpdateAssetLambdaHandler;
import com.element.enterpriseapi.core.person.UpdatePersonInputMessageConverter;
import com.element.enterpriseapi.core.person.UpdatePersonLambdaHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CommonConfiguration.class,
        MainframeDatasourceConfiguration.class,

        JdbcDealerAssignmentNumberConverter.class,
        JdbcOrgIdConverter.class,
        UpdatePersonLambdaHandler.class,
        UpdatePersonInputMessageConverter.class,
        DbFieldGroupAspect.class,

        UpdateAssetLambdaHandler.class,
        UpdateAssetInputMessageConverter.class

})
public class CoreConfiguration {
}