package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.common.EdbAssetId;
import com.element.enterpriseapi.common.EdbOrgId;
import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.common.SpinOrgId;
import com.element.enterpriseapi.lambda.LambdaInput;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Jacksonized
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MileageInput extends LambdaInput<MileageInput.Mileage> {


    @Data
    @Builder(toBuilder = true)
    @Jacksonized
    @SuppressWarnings("java:S116")
    public static class Mileage {
        Integer spin_mileage_rpt_id;
        Integer spin_psn_id;

        LocalDateTime period_start_dt;
        LocalDateTime period_end_dt;
        Integer days_in_veh;
        Integer rpt_period_typ_cd;
        Float bus_mileage_amt;
        Float psn_mileage_amt;
        Float end_odom_reading_amt;
        Float begin_odom_reading_amt;
        Integer mileage_rpt_source_cd;
        Integer suppress_ivr_export_ind;
        Integer suppress_ver_export_ind;
        LocalDateTime ver_export_dt;
        LocalDateTime ivr_export_dt;
        Integer commuter_trips_qty;

        SpinOrgId spin_psn_org_id;
        EdbOrgId edb_psn_org_id;
        @Size(max = 2)
        String psn_corp_cd;
        @Size(max = 8)
        String psn_cli_no;
        String psn_bkdn;

        SpinAssetId spin_asset_id;
        EdbAssetId edb_asset_id;
        SpinOrgId spin_asset_org_id;
        EdbOrgId edb_asset_org_id;
        @Size(max = 2)
        String asset_corp_cd;
        @Size(max = 8)
        String asset_cli_no;
        String asset_bkdn;

        List<MileageDetailInput> mileageReportDetail;
    }

}
