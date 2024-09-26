package com.element.enterpriseapi.gauge.odometer;

import com.element.enterpriseapi.WithJson;
import com.element.enterpriseapi.common.SpinAssetId;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OdometerInputMessageConverterTest implements WithJson {

    private static final String JSON = """
            {
                    "info": {
                        "fieldName": "InsertOdometerReading"
                    },
                    "arguments" : {
                        "input": {
                            "odom_reading_amt": 248,
                            "odom_reading_dt": "2024-06-04T14:02:24.123Z",
                            "odom_reading_typ_cd": "MANUAL",
                            "spin_asset_id": 987
                        }
                    },
                    "request": {
                        "headers": {
                            "x-client-identifier": "x2bsl",
                            "x-audit-login": "test-user"
                        }
                    }
                }
            """;

    private final OdometerInputMessageConverter converter = new OdometerInputMessageConverter(OBJECT_MAPPER);

    @Test
    void deserialize() {
        OdometerInput actual = (OdometerInput) converter.fromMessage(new GenericMessage<>(JSON), OdometerInput.class);
        assertThat(actual).isEqualTo(
                OdometerInput
                        .builder()
                        .auditLogin("test-user")
                        .auditProgram("x2bsl")
                        .value(OdometerInput.Odometer
                                .builder()
                                .odom_reading_amt(248L)
                                .odom_reading_dt(ZonedDateTime.parse("2024-06-04T14:02:24.123Z").toLocalDateTime())
                                .odom_reading_typ_cd("MANUAL")
                                .spin_asset_id(new SpinAssetId(987))
                                .build()
                        )
                        .build()
        );
    }

}
