package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.lambda.LambdaResponse;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("java:S116")
public class UpdateAssetResponse extends LambdaResponse {
    SpinAssetId spin_asset_id;
}
