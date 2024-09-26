package com.element.enterpriseapi.gauge.odometer;

import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.lambda.LambdaResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("java:S116")
public class OdometerResponse extends LambdaResponse {
    private SpinAssetId spin_asset_id;
}
