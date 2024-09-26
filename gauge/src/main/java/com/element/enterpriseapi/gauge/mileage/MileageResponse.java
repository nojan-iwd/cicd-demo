package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.lambda.LambdaResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("java:S116")
public class MileageResponse extends LambdaResponse {
    private Integer spin_mileage_rpt_id;
}
