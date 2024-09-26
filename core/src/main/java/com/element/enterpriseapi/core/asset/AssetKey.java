package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.common.SpinAssetId;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@SuppressWarnings("java:S116")
public class AssetKey {
    SpinAssetId spin_asset_id;
}
