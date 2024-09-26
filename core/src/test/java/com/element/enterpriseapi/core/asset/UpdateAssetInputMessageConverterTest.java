package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.WithJson;
import com.element.enterpriseapi.common.SpinAssetId;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateAssetInputMessageConverterTest implements WithJson {
    private static final String JSON = """
            {
                 "info": {
                     "fieldName": "UpdateAsset"
                 },
                 "arguments": {
                     "input": [
                         {
                             "key": {
                                 "spin_asset_id": 13176770
                             },
                             "data": {
                                 "bkdn": "RETL-GLOB-T0454-0510",
                                 "body_style": "LR RH SLIDE 148",
                                 "city_loc_cd": "0400",
                                 "cli_asset_id": "DECAL",
                                 "cnty_prov_loc_cd": "109",
                                 "competitor_unit_no": "1A",
                                 "contract_cd": "LS",
                                 "contract_no": "5778LX",
                                 "drvr_id": "1906969",
                                 "engine_cd": "G",
                                 "fac_ord_no": "KE2YL241",
                                 "gvw": 8600.0,
                                 "hvut_cd": "*",
                                 "invy_stat_cd": "4",
                                 "make": "TSLA",
                                 "model": "MODEL S",
                                 "model_cd": "E2Y",
                                 "model_yr": "19",
                                 "no_of_cyl": 6,
                                 "no_of_doors": "3",
                                 "out_of_stock_ind": "N",
                                 "phh_asset_id": "UNKNOWN",
                                 "prev_cli_asset_id": "ABC",
                                 "prev_cli_no": "ABC",
                                 "prev_unit_no": "ABC",
                                 "prod_class_cd": "LT",
                                 "spin_org_id": "20268",
                                 "unit_no": "720276",
                                 "vin": "1FTYE2YM7",
                                 "series": "CARGOX",
                                 "st_loc_cd": "20",
                                 "spin_competitor_org_id": 10,
                                 "driverInfo": {
                                     "drvr_mid_nm": "Logan",
                                     "drvr_chng_dt": "2015-04-30"
                                 }
                             }
                         }
                     ]
                 },
                 "request": {
                     "headers": {
                         "x-client-identifier": "x2bsl",
                         "x-audit-login": "test-user"
                     }
                 }
             }
            """;
    private final UpdateAssetInputMessageConverter converter = new UpdateAssetInputMessageConverter(OBJECT_MAPPER);

    @Test
    void deserialize() {
        UpdateAssetInput actual = (UpdateAssetInput) converter.fromMessage(new GenericMessage<>(JSON), UpdateAssetInput.class);
        assertThat(actual).isEqualTo(
                UpdateAssetInput
                        .builder()
                        .auditLogin("test-user")
                        .auditProgram("x2bsl")
                        .value(List.of(
                                UpdateAssetInput.Asset.builder()
                                        .key(
                                                AssetKey
                                                        .builder()
                                                        .spin_asset_id(new SpinAssetId(13176770))
                                                        .build()
                                        )
                                        .data(
                                                AssetInput
                                                        .builder()
                                                        .bkdn("RETL-GLOB-T0454-0510")
                                                        .body_style("LR RH SLIDE 148")
                                                        .city_loc_cd("0400")
                                                        .cli_asset_id("DECAL")
                                                        .cnty_prov_loc_cd("109")
                                                        .competitor_unit_no("1A")
                                                        .contract_cd("LS")
                                                        .contract_no("5778LX")
                                                        .drvr_id("1906969")
                                                        .engine_cd("G")
                                                        .fac_ord_no("KE2YL241")
                                                        .gvw(BigDecimal.valueOf(8600.0))
                                                        .hvut_cd("*")
                                                        .invy_stat_cd("4")
                                                        .make("TSLA")
                                                        .model("MODEL S")
                                                        .model_cd("E2Y")
                                                        .model_yr("19")
                                                        .no_of_cyl(6)
                                                        .no_of_doors("3")
                                                        .out_of_stock_ind("N")
                                                        .phh_asset_id("UNKNOWN")
                                                        .prod_class_cd("LT")
                                                        .spin_org_id(20268)
                                                        .unit_no("720276")
                                                        .vin("1FTYE2YM7")
                                                        .series("CARGOX")
                                                        .st_loc_cd("20")
                                                        .spin_competitor_org_id(10)
                                                        .prev_cli_no("ABC")
                                                        .prev_unit_no("ABC")
                                                        .prev_cli_asset_id("ABC")
                                                        .driverInfo(
                                                                AssetDriverInfo.builder()
                                                                        .drvr_nm_midinit("L")
                                                                        .drvr_nm_midrest("ogan")
                                                                        .drvr_chng_dt_day(30)
                                                                        .drvr_chng_dt_month(4)
                                                                        .drvr_chng_dt_year(15)
                                                                        .drvr_mid_nm("Logan")
                                                                        .drvr_chng_dt(LocalDate.parse("2015-04-30"))
                                                                .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        ))
                        .build()
        );
    }

}
