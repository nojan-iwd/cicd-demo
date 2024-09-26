package com.element.enterpriseapi.gauge.odometer;

import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.lambda.LambdaInput;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Jacksonized
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OdometerInput extends LambdaInput<OdometerInput.Odometer> {

    @Data
    @Builder(toBuilder = true)
    @Jacksonized
    @SuppressWarnings("java:S116")
    public static class Odometer {
        Long odom_reading_amt;
        LocalDateTime odom_reading_dt;
        String odom_reading_typ_cd;
        SpinAssetId spin_asset_id;
    }
}
