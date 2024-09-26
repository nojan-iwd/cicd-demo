package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.BaseIntegrationTest;
import com.element.enterpriseapi.IntegrationTestUtils;
import com.element.enterpriseapi.common.EdbAssetId;
import com.element.enterpriseapi.common.EdbOrgId;
import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.common.SpinOrgId;
import org.assertj.core.api.WithAssertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;

@SpringBootTest
@Transactional(transactionManager = "edbTxManager")
@Rollback
public class MileageIntegrationTest extends BaseIntegrationTest implements WithAssertions {

    private static final SpinAssetId SPIN_ASSET_ID = new SpinAssetId(999_999_2);
    private static final EdbAssetId EDB_ASSET_ID = new EdbAssetId(888_888_2);
    private static final SpinOrgId SPIN_ASSET_ORG_ID = new SpinOrgId(777_777_1);
    private static final EdbOrgId EDB_ASSET_ORG_ID = new EdbOrgId(666_666_1);
    private static final SpinOrgId SPIN_PERSON_ORG_ID = new SpinOrgId(777_777_2);
    private static final EdbOrgId EDB_PERSON_ORG_ID = new EdbOrgId(666_666_2);

    private static final Integer MILEAGE_REPORT_ID = 1;
    private static final String USERNAME = "username";
    private static final Integer PERSON_ID = 123;
    private static final String CORP_CD_CA = "CA";
    private static final String CORP_CD_US = "US";
    private static final Integer UPDATED_EXPENSE_QUANTITY = 9;
    private static final BigDecimal UPDATED_EXPENSE_AMOUNT = new BigDecimal("9999.00");

    private final InsertMileageLambdaHandler insertMileageLambdaHandler;
    private final UpdateMileageLambdaHandler updateMileageLambdaHandler;
    private final IntegrationTestUtils testUtils;

    public MileageIntegrationTest(@Autowired InsertMileageLambdaHandler insertMileageLambdaHandler,
                                  @Autowired UpdateMileageLambdaHandler updateMileageLambdaHandler,
                                  @Autowired @Qualifier("edbNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.insertMileageLambdaHandler = insertMileageLambdaHandler;
        this.updateMileageLambdaHandler = updateMileageLambdaHandler;
        this.testUtils = new IntegrationTestUtils(namedJdbcTemplate.getJdbcTemplate());
    }

    @BeforeEach
    public void beforeEach() {
        testUtils.insertAssetIdMapping(SPIN_ASSET_ID, EDB_ASSET_ID);
        testUtils.insertOrgIdMapping(SPIN_ASSET_ORG_ID, EDB_ASSET_ORG_ID);
        testUtils.insertOrgIdMapping(SPIN_PERSON_ORG_ID, EDB_PERSON_ORG_ID);
    }

    @Test
    void shouldInsertMileageReport() {
        MileageInput input = createMileageInput(CORP_CD_CA);
        var response = insertMileageLambdaHandler.apply(input);

        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        Map<String, Object> mileageReportRow = testUtils.readRow(
                "txnd1_driver_mileage_cur.mileage_report",
                "ast_id=? and psn_id=?", EDB_ASSET_ID.getValue(), PERSON_ID);
        Integer mileageReportDetailCount = testUtils.countRows(
                "txnd1_driver_mileage_cur.mileage_report_detail",
                "ast_id=? and psn_id=?", EDB_ASSET_ID.getValue(), PERSON_ID);

        assertThat(mileageReportRow.get("mileage_rpt_id")).isEqualTo(MILEAGE_REPORT_ID);
        assertThat(mileageReportRow.get("ast_id")).isEqualTo(EDB_ASSET_ID.getValue());
        assertThat(mileageReportRow.get("ast_org_id")).isEqualTo(EDB_ASSET_ORG_ID.getValue());
        assertThat(mileageReportRow.get("psn_org_id")).isEqualTo(EDB_PERSON_ORG_ID.getValue());
        assertThat(mileageReportRow.get("psn_corp_cd")).isEqualTo(CORP_CD_CA);
        assertThat(mileageReportDetailCount).isEqualTo(input.getValue().getMileageReportDetail().size());
    }

    @Test
    void shouldNotInsertMileageReportDetail() {
        MileageInput input = createMileageInput(CORP_CD_US);
        var response = insertMileageLambdaHandler.apply(input);

        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        Map<String, Object> mileageReportRow = testUtils.readRow(
                "txnd1_driver_mileage_cur.mileage_report",
                "ast_id=? and psn_id=?", EDB_ASSET_ID.getValue(), PERSON_ID);
        Integer mileageReportDetailCount = testUtils.countRows(
                "txnd1_driver_mileage_cur.mileage_report_detail",
                "ast_id=? and psn_id=?", EDB_ASSET_ID.getValue(), PERSON_ID);

        assertThat(mileageReportRow.get("mileage_rpt_id")).isEqualTo(MILEAGE_REPORT_ID);
        assertThat(mileageReportRow.get("ast_id")).isEqualTo(EDB_ASSET_ID.getValue());
        assertThat(mileageReportRow.get("psn_corp_cd")).isEqualTo(CORP_CD_US);
        assertThat(mileageReportDetailCount).isEqualTo(0);
    }

    @Test
    void duplicateInsertFails() {
        MileageInput input = createMileageInput(CORP_CD_US);
        insertMileageLambdaHandler.apply(input);
        var response = insertMileageLambdaHandler.apply(input);
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Mileage Report entry already exists");
    }

    @Test
    void shouldUpdateMileageReport() {
        MileageInput input = createMileageInput(CORP_CD_CA);
        insertMileageLambdaHandler.apply(input);

        var response = updateMileageLambdaHandler.apply(updateMileageInput(input));
        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        Map<String, Object> mileageReportRow = testUtils.readRow(
                "txnd1_driver_mileage_cur.mileage_report",
                "ast_id=? and psn_id=?", EDB_ASSET_ID.getValue(), PERSON_ID);

        assertThat(mileageReportRow.get("mileage_rpt_id")).isEqualTo(MILEAGE_REPORT_ID);
        assertThat(mileageReportRow.get("ast_id")).isEqualTo(EDB_ASSET_ID.getValue());
        assertThat(mileageReportRow.get("begin_odom_reading_amt")).isEqualTo(BigDecimal.valueOf(2_000.0));
        assertThat(mileageReportRow.get("end_odom_reading_amt")).isEqualTo(BigDecimal.valueOf(10_000.0));
        assertThat(mileageReportRow.get("bus_mileage_amt")).isEqualTo(BigDecimal.valueOf(1_000.0));
        assertThat(mileageReportRow.get("psn_mileage_amt")).isEqualTo(BigDecimal.valueOf(200.0));
        assertThat(mileageReportRow.get("audit_update_login")).isEqualTo(input.getAuditLogin());
        assertThat(mileageReportRow.get("audit_update_pgm")).isEqualTo(input.getAuditProgram());
        assertThat(mileageReportRow.get("audit_update_dt")).isNotNull();
    }

    @Test
    void shouldUpdateMileageReportDetail() {
        MileageInput input = createMileageInput(CORP_CD_CA);
        insertMileageLambdaHandler.apply(input);

        var response = updateMileageLambdaHandler.apply(updateMileageDetailInput(input));
        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        List<Map<String, Object>> mileageReportDetailRows = testUtils.readRows(
                "txnd1_driver_mileage_cur.mileage_report_detail",
                List.of("exp_qnty, cast(exp_amt as numeric)"),
                "mileage_rpt_id=? and psn_id=?", MILEAGE_REPORT_ID, PERSON_ID);

        mileageReportDetailRows.forEach(row -> {
            assertThat(row.get("exp_qnty")).isEqualTo(UPDATED_EXPENSE_QUANTITY);
            assertThat(row.get("exp_amt")).isEqualTo(UPDATED_EXPENSE_AMOUNT);
        });
    }

    @Test
    void updateMileageReportThatDoesNotExist() {
        MileageInput input = createMileageInput(CORP_CD_US);
        var response = updateMileageLambdaHandler.apply(input);
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Mileage Report entry doesn't exist");
    }

    private static MileageInput updateMileageInput(MileageInput input) {
        return input
                .toBuilder()
                .value(
                        input
                                .getValue()
                                .toBuilder()
                                .end_odom_reading_amt(10_000F)
                                .begin_odom_reading_amt(2_000F)
                                .bus_mileage_amt(1000F)
                                .psn_mileage_amt(200F)
                                .build()
                )
                .build();
    }

    private static MileageInput updateMileageDetailInput(MileageInput input) {
        input.getValue().getMileageReportDetail().forEach(detail -> {
            detail.setExp_qnty(UPDATED_EXPENSE_QUANTITY);
            detail.setExp_amt(UPDATED_EXPENSE_AMOUNT);
        });
        return input;
    }

    private static MileageInput createMileageInput(String corpCd) {
        MileageInput.Mileage mileage = Instancio.of(MileageInput.Mileage.class)
                .set(field(MileageInput.Mileage::getSpin_psn_id), PERSON_ID)
                .set(field(MileageInput.Mileage::getSpin_mileage_rpt_id), MILEAGE_REPORT_ID)
                .set(field(MileageInput.Mileage::getSpin_asset_id), SPIN_ASSET_ID)
                .set(field(MileageInput.Mileage::getSpin_asset_org_id), SPIN_ASSET_ORG_ID)
                .set(field(MileageInput.Mileage::getSpin_psn_org_id), SPIN_PERSON_ORG_ID)
                .set(field(MileageInput.Mileage::getPsn_corp_cd), corpCd)
                .set(field(MileageInput.Mileage::getAsset_corp_cd), corpCd)
                .create();

        return Instancio.of(MileageInput.class)
                .set(field(MileageInput::getAuditLogin), USERNAME)
                .set(field(MileageInput::getValue), mileage)
                .set(field(MileageInput::getOtlpTraceParent), null)
                .create();
    }
}
