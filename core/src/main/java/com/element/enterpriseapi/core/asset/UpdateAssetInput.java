package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.lambda.LambdaInput;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateAssetInput extends LambdaInput<List<UpdateAssetInput.Asset>> {

    @Value
    @Builder
    @Jacksonized
    public static class Asset {
        AssetKey key;
        AssetInput data;
    }
}
