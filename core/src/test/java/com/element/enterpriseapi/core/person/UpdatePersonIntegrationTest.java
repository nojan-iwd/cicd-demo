package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.BaseIntegrationTest;
import com.element.enterpriseapi.IntegrationTestUtils;
import com.element.enterpriseapi.common.SpinPersonId;
import com.element.enterpriseapi.core.CoreConfiguration;
import com.element.enterpriseapi.core.common.CommType;
import com.element.enterpriseapi.core.common.EmailAddressInfoInput;
import com.element.enterpriseapi.core.common.PhoneNumberInfoInput;
import com.element.enterpriseapi.core.common.PostalAddressInfoInput;
import com.element.enterpriseapi.lambda.LambdaInput;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Import(CoreConfiguration.class)
@Transactional(transactionManager = "mainframeTxManager")
@Rollback
public class UpdatePersonIntegrationTest extends BaseIntegrationTest implements WithAssertions {
    private static final SpinPersonId SPIN_PERSON_ID = new SpinPersonId(999);

    private final UpdatePersonLambdaHandler lambdaHandler;
    private final IntegrationTestUtils testUtils;

    public UpdatePersonIntegrationTest(@Autowired UpdatePersonLambdaHandler lambdaHandler,
                                       @Autowired @Qualifier("mainframeNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.lambdaHandler = lambdaHandler;
        this.testUtils = new IntegrationTestUtils(namedJdbcTemplate.getJdbcTemplate());
    }

    @Test
    void insertingCommRecord() {
        insertAddress();

        UpdatePersonInput input = createUpdatePersonInput(true);
        UpdatePersonInput.Person entry = input.getValue().getFirst();
        var response = lambdaHandler.apply(input).getFirst();

        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        assertAddressUpdated(input, entry);
        assertEmailUpdated(input, entry, true);
        assertCellularPhoneUpdated(input, entry);
    }

    @Test
    void updatingCommRecord() {
        insertAddress();
        insertEmailCommRecord();

        UpdatePersonInput input = createUpdatePersonInput(true);
        UpdatePersonInput.Person entry = input.getValue().getFirst();
        var response = lambdaHandler.apply(input).getFirst();

        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        assertAddressUpdated(input, entry);
        assertEmailUpdated(input, entry, false);
        assertCellularPhoneUpdated(input, entry);
    }

    @Test
    void deletingCommRecord() {
        insertAddress();
        insertEmailCommRecord();

        UpdatePersonInput input = createUpdatePersonInput(false);
        UpdatePersonInput.Person entry = input.getValue().getFirst();

        var response = lambdaHandler.apply(input).getFirst();

        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        assertAddressUpdated(input, entry);
        assertEmailDeleted();
        assertCellularPhoneUpdated(input, entry);
    }

    private void assertAddressUpdated(LambdaInput<?> input, UpdatePersonInput.Person entry) {
        var row = testUtils.readRow("sysusr.psn_addr", "psn_id=?", SPIN_PERSON_ID.getValue());
        assertThat(row.get("addr_line1").toString().trim()).isEqualTo(entry.getPrimaryAddress().getAddr_line1());
        assertThat(row.get("addr_line2").toString().trim()).isEqualTo(entry.getPrimaryAddress().getAddr_line2());
        assertThat(row.get("addr_line3").toString().trim()).isEqualTo(entry.getPrimaryAddress().getAddr_line3());
        assertThat(row.get("addr_line4").toString().trim()).isEqualTo(entry.getPrimaryAddress().getAddr_line4());
        assertThat(row.get("city_nm").toString().trim()).isEqualTo(entry.getPrimaryAddress().getCity_nm());
        assertThat(row.get("cnty_nm").toString().trim()).isEqualTo(entry.getPrimaryAddress().getCnty_nm());
        assertThat(row.get("st_prov_abbr_cd").toString().trim()).isEqualTo(entry.getPrimaryAddress().getSt_prov_abbr_cd());
        assertThat(row.get("iso_cntry_cd").toString().trim()).isEqualTo(entry.getPrimaryAddress().getIso_cntry_cd());
        assertThat(row.get("postcode").toString().trim()).isEqualTo(entry.getPrimaryAddress().getPostcode());
        assertThat(row.get("pref_addr_ind").toString().trim()).isEqualTo("1");
        assertThat(row.get("audit_update_login").toString().trim()).isEqualTo(input.getAuditLogin());
        assertThat(row.get("audit_update_pgm").toString().trim()).isEqualTo(input.getAuditProgram());

        row = testUtils.readRow("sysusr.psn_chng_hist", "psn_id=? and psn_data_cd=16", SPIN_PERSON_ID.getValue());
        assertThat(row.get("psn_data_cd").toString().trim()).isEqualTo("16");
        assertThat(row.get("chng_from_val").toString().trim()).isEqualTo("1                                       2                                       3                                       4                                       5                                       6 7         8CA0");
        assertThat(row.get("chng_to_val").toString().trim()).isEqualTo("LINE 1                                  LINE 2                                  LINE 3                                  LINE 4                                  CITY A                                REGION BON     12345CA1");
        assertThat(row.get("audit_insert_login").toString().trim()).isEqualTo(input.getAuditLogin());
        assertThat(row.get("audit_insert_login_nm").toString().trim()).isEqualTo(input.getAuditLogin());
        assertThat(row.get("audit_insert_pgm").toString().trim()).isEqualTo(input.getAuditProgram());
        assertThat(row.get("tcv_audit_insert_userid").toString().trim()).isEqualTo(input.getAuditLogin());
        assertThat(row.get("tcv_audit_insert_program").toString().trim()).isEqualTo(input.getAuditProgram());
    }

    private void assertEmailUpdated(LambdaInput<?> input, UpdatePersonInput.Person entry, boolean insert) {
        var row = testUtils.readRow("sysusr.psn_comm", "psn_id=? and comm_typ_cd=?", SPIN_PERSON_ID.getValue(), CommType.ALTERNATE_EMAIL.getCode());
        assertThat(row.get("comm_val").toString().trim()).isEqualTo(entry.getAlternateEmailAddress().getEmail_addr());
        assertThat(row.get("pref_mthd_ind").toString()).isEqualTo("1");
        assertCommRecordAuditFields(row, input, insert);
    }

    private void assertEmailDeleted() {
        int count = testUtils.countRows("sysusr.psn_comm", "psn_id=? and comm_typ_cd=?", SPIN_PERSON_ID.getValue(), CommType.ALTERNATE_EMAIL.getCode());
        assertThat(count).isEqualTo(0);
    }

    private void assertCellularPhoneUpdated(LambdaInput<?> input, UpdatePersonInput.Person entry) {
        var row = testUtils.readRow("sysusr.psn_comm", "psn_id=? and comm_typ_cd=?", SPIN_PERSON_ID.getValue(), CommType.CELL_PHONE.getCode());
        assertThat(row.get("comm_val").toString().trim()).isEqualTo(entry.getCellularPhone().getPhone_no());
        assertThat(row.get("comm_mask").toString().trim()).isEqualTo(entry.getCellularPhone().getPhone_no_mask());
        assertThat(row.get("ext_pager_pin").toString().trim()).isEqualTo(entry.getCellularPhone().getExt_pager_pin());
        assertThat(row.get("pref_mthd_ind").toString()).isEqualTo("1");
        assertCommRecordAuditFields(row, input, true);
    }

    private void assertCommRecordAuditFields(Map<String, Object> row, LambdaInput<?> input, boolean insert) {
        if (insert) {
            assertThat(row.get("audit_insert_login").toString().trim()).isEqualTo(input.getAuditLogin());
            assertThat(row.get("audit_insert_pgm").toString().trim()).isEqualTo(input.getAuditProgram());
            assertThat(row.get("tcv_audit_insert_userid").toString().trim()).isEqualTo(input.getAuditLogin());
            assertThat(row.get("tcv_audit_insert_program").toString().trim()).isEqualTo(input.getAuditProgram());
        } else {
            assertThat(row.get("audit_update_login").toString().trim()).isEqualTo(input.getAuditLogin());
            assertThat(row.get("audit_update_pgm").toString().trim()).isEqualTo(input.getAuditProgram());
            assertThat(row.get("tcv_audit_update_userid").toString().trim()).isEqualTo(input.getAuditLogin());
            assertThat(row.get("tcv_audit_update_program").toString().trim()).isEqualTo(input.getAuditProgram());
        }
    }

    private static UpdatePersonInput createUpdatePersonInput(boolean includeEmail) {
        EmailAddressInfoInput email = EmailAddressInfoInput.builder()
                .email_addr("jdoe@gmail.com")
                .pref_mthd_ind(true)
                .build();

        return UpdatePersonInput
                .builder()
                .value(
                        List.of(
                                UpdatePersonInput
                                        .Person
                                        .builder()
                                        .key(PersonKey.builder().spin_psn_id(SPIN_PERSON_ID).build())
                                        .data(PersonInput.builder()
                                                .primaryAddress(
                                                        PostalAddressInfoInput.builder()
                                                                .addr_line1("LINE 1")
                                                                .addr_line2("LINE 2")
                                                                .addr_line3("LINE 3")
                                                                .addr_line4("LINE 4")
                                                                .city_nm("CITY A")
                                                                .cnty_nm("REGION B")
                                                                .st_prov_abbr_cd("ON")
                                                                .iso_cntry_cd("CA")
                                                                .postcode("12345")
                                                                .pref_addr_ind(true)
                                                                .build()
                                                )
                                                .alternateEmailAddress(includeEmail ? email : null)
                                                .cellularPhone(
                                                        PhoneNumberInfoInput.builder()
                                                                .phone_no("4164169988")
                                                                .ext_pager_pin("123")
                                                                .phone_no_mask("***")
                                                                .pref_mthd_ind(true)
                                                                .build()
                                                )
                                                .build())
                                        .build()
                        )
                )
                .auditLogin("jdoe")
                .auditProgram("x2")
                .build();
    }

    private void insertAddress() {
        testUtils.executeSQL(
                """
                           INSERT INTO sysusr.psn_addr (
                            psn_id,
                            addr_typ_cd,
                            addr_line1,
                            addr_line2,
                            addr_line3,
                            addr_line4,
                            city_nm,
                            cnty_nm,
                            st_prov_abbr_cd,
                            postcode,
                            iso_cntry_cd,
                            pref_addr_ind,audit_insert_dt,
                            audit_insert_tm,
                            audit_insert_login,
                            audit_insert_pgm,
                            tcv_audit_insert_date,
                            tcv_audit_insert_userid,
                            tcv_audit_insert_program
                           )
                           VALUES(
                            999,
                            39,
                            '1',
                            '2',
                            '3',
                            '4',
                            '5',
                            '6',
                            '7',
                            '8',
                            'CA',
                            0,
                            CURRENT_DATE,
                            CURRENT_TIMESTAMP,
                            '',
                            '',
                            CURRENT_TIMESTAMP,
                            '',
                            ''
                           );
                        """);
    }

    private void insertEmailCommRecord() {
        testUtils.executeSQL(
                """        
                        INSERT INTO sysusr.psn_comm (
                            psn_id,
                            comm_typ_cd,
                            comm_val,
                            comm_mask,
                            ext_pager_pin,
                            pref_mthd_ind,
                            audit_insert_dt,
                            audit_insert_tm,
                            audit_insert_login,
                            audit_insert_pgm,
                            tcv_audit_insert_date,
                            tcv_audit_insert_userid,
                            tcv_audit_insert_program
                        ) VALUES (
                            999,
                            49,
                            'email@old.com',
                            '',
                            '',
                            1,
                            CURRENT_DATE,
                            CURRENT_TIMESTAMP,
                            '',
                            '',
                            CURRENT_TIMESTAMP,
                            '',
                            ''
                        )
                        """
        );
    }
}
