package com.element.enterpriseapi.gauge.odometer;

import com.element.enterpriseapi.BaseIntegrationTest;
import com.element.enterpriseapi.IntegrationTestUtils;
import com.element.enterpriseapi.common.EdbAssetId;
import com.element.enterpriseapi.common.SpinAssetId;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest
@Transactional(transactionManager = "edbTxManager")
@Rollback
public class OdometerIntegrationTest extends BaseIntegrationTest implements WithAssertions {
    private static final SpinAssetId SPIN_ASSET_ID = new SpinAssetId(999_999_1);
    private static final EdbAssetId EDB_ASSET_ID = new EdbAssetId(888_888_1);
    private static final LocalDateTime READING_TIMESTAMP = LocalDateTime.parse("2024-06-07T17:04:03.123");
    private static final String READING_TYPE = "CWD";
    private static final int READING_AMOUNT = 10_000;

    private final InsertOdometerReadingLambdaHandler lambdaHandler;
    private final IntegrationTestUtils testUtils;

    public OdometerIntegrationTest(@Autowired InsertOdometerReadingLambdaHandler lambdaHandler,
                                   @Autowired @Qualifier("edbNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.lambdaHandler = lambdaHandler;
        this.testUtils = new IntegrationTestUtils(namedJdbcTemplate.getJdbcTemplate());
    }

    @BeforeEach
    public void beforeEach() {
        testUtils.insertAssetIdMapping(SPIN_ASSET_ID, EDB_ASSET_ID);
    }

    @Test
    void shouldInsertOdometerReading() {
        OdometerInput input = createInput();
        var response = lambdaHandler.apply(input);
        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        Map<String, Object> row = testUtils.readRow("astd1_asset.veh_odom", "ast_id=?", EDB_ASSET_ID.getValue());
        assertThat(row.get("ast_id")).isEqualTo(EDB_ASSET_ID.getValue());
        assertThat(row.get("odom_rdng_amt")).isEqualTo(READING_AMOUNT);
    }

    @Test
    void duplicateInsertFails() {
        OdometerInput input = createInput();
        lambdaHandler.apply(input);
        var response = lambdaHandler.apply(input);
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Odometer entry already exists");
    }

    private static OdometerInput createInput() {
        return OdometerInput.builder()
                .value(OdometerInput.Odometer.builder()
                        .spin_asset_id(SPIN_ASSET_ID)
                        .odom_reading_amt((long) READING_AMOUNT)
                        .odom_reading_dt(READING_TIMESTAMP)
                        .odom_reading_typ_cd(READING_TYPE)
                        .build())
                .build();
    }

}
