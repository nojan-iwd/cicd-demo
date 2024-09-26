package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.WithJson;
import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.common.SpinOrgId;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MileageInputMessageConverterTest implements WithJson {

    private static final String JSON = """
            {
                "info": {
                    "fieldName": "CreateMileageReport"
                },
                "arguments": {
                    "input": {
                        "spin_psn_id": 2060994,
                        "period_end_dt": "2024-04-30T00:00:00.000Z",
                        "period_start_dt": "2024-04-01T00:00:00.000Z",
                        "days_in_veh": 30,
                        "spin_asset_id": 13001011,
                        "rpt_period_typ_cd": 12,
                        "bus_mileage_amt": 2000,
                        "psn_mileage_amt": 200,
                        "end_odom_reading_amt": 7800.0,
                        "begin_odom_reading_amt": 5800.0,
                        "mileage_rpt_source_cd": 1,
                        "suppress_ivr_export_ind": 0,
                        "suppress_ver_export_ind": 0,
                        "spin_psn_org_id": 8,
                        "psn_corp_cd": "CA",
                        "psn_cli_no": "0008",
                        "psn_bkdn": "NORTHERN.REGION",
                        "spin_asset_org_id": 8,
                        "asset_corp_cd": "CA",
                        "asset_cli_no": "0008",
                        "asset_bkdn": "NORTHERN.REGION",
                        "commuter_trips_qty" : 0,
                        "mileageReportDetail" : [
                            {
                            "exp_typ_cd": "FUEL",
                            "uom_typ_cd": "L" ,
                            "exp_amt": 75.0,
                            "exp_qnty": 50
                            },
                            {
                            "exp_typ_cd": "OIL",
                            "uom_typ_cd": "L",
                            "exp_amt": 30.0,
                            "exp_qnty": 2 
                            }, 
                            {
                            "exp_typ_cd": "PARK",
                            "uom_typ_cd": "" ,
                            "exp_amt": 15.0,
                            "exp_qnty": 0 
                            },
                            {
                            "exp_typ_cd": "TOLL",
                            "uom_typ_cd": "" ,
                            "exp_amt": 7.0,
                            "exp_qnty": 0 
                            },
                            {
                            "exp_typ_cd": "ACRP",
                            "uom_typ_cd": "" ,
                            "exp_amt": 100.0,
                            "exp_qnty": 0 
                            },
                            {
                            "exp_typ_cd": "MISC",
                            "uom_typ_cd": "" ,
                            "exp_amt": 20.0,
                            "exp_qnty": 0 
                            }, 
                            {
                            "exp_typ_cd": "PUC",
                            "uom_typ_cd": "" ,
                            "exp_amt": 25.0,
                            "exp_qnty": 0 
                            },
                            {
                            "exp_typ_cd": "MAIN",
                            "uom_typ_cd": "" ,
                            "exp_amt": 20.0,
                            "exp_qnty": 0 
                            }
                        ]
                    }
                },
                "request": {
                    "headers": {
                        "x-client-identifier": "x2bsl",
                        "x-audit-login": "test-user"
                    }
                }
            }
                            }
            """;

    private final MileageInputMessageConverter converter = new MileageInputMessageConverter(OBJECT_MAPPER);

    @Test
    void deserialize() {
        MileageInput actual = (MileageInput) converter.fromMessage(new GenericMessage<>(JSON), MileageInput.class);
        assertThat(actual).isEqualTo(
                MileageInput
                        .builder()
                        .auditLogin("test-user")
                        .auditProgram("x2bsl")
                        .value(MileageInput.Mileage
                                .builder()
                                .spin_psn_id(2060994)
                                .period_end_dt(ZonedDateTime.parse("2024-04-30T00:00:00.000Z").toLocalDateTime())
                                .period_start_dt(ZonedDateTime.parse("2024-04-01T00:00:00.000Z").toLocalDateTime())
                                .days_in_veh(30)
                                .spin_asset_id(new SpinAssetId(13001011))
                                .rpt_period_typ_cd(12)
                                .bus_mileage_amt(2000f)
                                .psn_mileage_amt(200f)
                                .end_odom_reading_amt(7800.0f)
                                .begin_odom_reading_amt(5800.0f)
                                .mileage_rpt_source_cd(1)
                                .suppress_ivr_export_ind(0)
                                .suppress_ver_export_ind(0)
                                .spin_psn_org_id(new SpinOrgId(8))
                                .psn_corp_cd("CA")
                                .psn_cli_no("0008")
                                .psn_bkdn("NORTHERN.REGION")
                                .spin_asset_org_id(new SpinOrgId(8))
                                .asset_corp_cd("CA")
                                .asset_cli_no("0008")
                                .asset_bkdn("NORTHERN.REGION")
                                .commuter_trips_qty(0)
                                .mileageReportDetail(List.of(
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("FUEL")
                                                .uom_typ_cd("L")
                                                .exp_qnty(50)
                                                .exp_amt(new BigDecimal("75.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("OIL")
                                                .uom_typ_cd("L")
                                                .exp_qnty(2)
                                                .exp_amt(new BigDecimal("30.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("PARK")
                                                .uom_typ_cd("")
                                                .exp_qnty(0)
                                                .exp_amt(new BigDecimal("15.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("TOLL")
                                                .uom_typ_cd("")
                                                .exp_qnty(0)
                                                .exp_amt(new BigDecimal("7.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("ACRP")
                                                .uom_typ_cd("")
                                                .exp_qnty(0)
                                                .exp_amt(new BigDecimal("100.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("MISC")
                                                .uom_typ_cd("")
                                                .exp_qnty(0)
                                                .exp_amt(new BigDecimal("20.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("PUC")
                                                .uom_typ_cd("")
                                                .exp_qnty(0)
                                                .exp_amt(new BigDecimal("25.0")).build(),
                                        MileageDetailInput.builder()
                                                .exp_typ_cd("MAIN")
                                                .uom_typ_cd("")
                                                .exp_qnty(0)
                                                .exp_amt(new BigDecimal("20.0")).build()))
                                .build()
                        ).build()
        );
    }
}
