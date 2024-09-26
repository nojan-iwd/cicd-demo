package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.common.AbstractLambdaHandler;
import com.element.enterpriseapi.common.CompositeBeanPropertySqlParameterSource;
import com.element.enterpriseapi.common.DataMutationException;
import com.element.enterpriseapi.common.FQN;
import com.element.enterpriseapi.core.common.*;
import com.element.enterpriseapi.exception.InputValidationException;
import com.element.enterpriseapi.lambda.LambdaInput;
import io.opentelemetry.api.OpenTelemetry;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.element.enterpriseapi.core.common.PsnDataCode.PRIMARY_POSTAL_ADDRESS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Slf4j
@Qualifier("UpdatePerson")
public class UpdatePersonLambdaHandler extends AbstractLambdaHandler<UpdatePersonInput, List<UpdatePersonResponse>> {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    protected final String psnAddrTable;
    protected final String psnCommTable;
    protected final String psnChngHistTable;

    public UpdatePersonLambdaHandler(
            @Qualifier("mainframeTxManager") PlatformTransactionManager txManager,
            @Qualifier("mainframeNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            FQN fqn,
            OpenTelemetry otlp
    ) {
        super(txManager, otlp);
        this.namedJdbcTemplate = namedParameterJdbcTemplate;
        this.psnAddrTable = fqn.resolvePostgres("sysusr", "psn_addr");
        this.psnCommTable = fqn.resolvePostgres("sysusr", "psn_comm");
        this.psnChngHistTable = fqn.resolvePostgres("sysusr", "psn_chng_hist");
    }

    @Override
    public void validateInput(UpdatePersonInput input) throws InputValidationException {
        // temporarily returning OK, should be calling EAPI validation framework
    }

    @Override
    public List<UpdatePersonResponse> executeInTransaction(UpdatePersonInput input) {
        return input
                .getValue()
                .stream()
                .map(e -> new UpdatePersonEntryProcessor(input, e).process())
                .toList();
    }

    @RequiredArgsConstructor
    private class UpdatePersonEntryProcessor {
        private final LambdaInput<?> input;
        private final UpdatePersonInput.Person person;

        public UpdatePersonResponse process() {
            updatePrimaryAddress();
            updateAlternateEmailAddress();
            updateCellularPhone();

            return UpdatePersonResponse.builder()
                    .success(true)
                    .rowCount(1)
                    .spin_psn_id(person.getSpin_psn_id())
                    .build();
        }

        private void updatePrimaryAddress() {
            if (person.getPrimaryAddress() == null) {
                return;
            }

            PostalAddressInfoInput existingRecord;
            try {
                existingRecord = namedJdbcTemplate.queryForObject(
                        String.format("SELECT * FROM %s WHERE psn_id=:psn_id and addr_typ_cd=:addr_typ_cd", psnAddrTable),
                        new MapSqlParameterSource(
                                Map.of(
                                        "psn_id", person.getSpin_psn_id().getValue(),
                                        "addr_typ_cd", AddressType.PRIMARY.getCode()
                                )
                        ),
                        new BeanPropertyRowMapper<>(PostalAddressInfoInput.class)
                );
            } catch (EmptyResultDataAccessException e) {
                throw new DataMutationException("Person primary address does not exist for spin_person_id=" + person.getSpin_psn_id());
            }

            int updatedRecords = namedJdbcTemplate.update(
                    """
                                UPDATE %s SET
                                    addr_line1=:addr_line1,
                                    addr_line2=:addr_line2,
                                    addr_line3=:addr_line3,
                                    addr_line4=:addr_line4,
                                    city_nm=:city_nm,
                                    cnty_nm=:cnty_nm,
                                    st_prov_abbr_cd=:st_prov_abbr_cd,
                                    postcode=:postcode,
                                    iso_cntry_cd=:iso_cntry_cd,
                                    pref_addr_ind=:pref_addr_ind,
                                    audit_update_dt=CURRENT_DATE,
                                    audit_update_tm=CURRENT_TIMESTAMP,
                                    audit_update_login=:audit_update_login,
                                    audit_update_pgm=:audit_update_pgm
                                WHERE psn_id=:psn_id and addr_typ_cd=:addr_typ_cd
                            """.formatted(psnAddrTable),
                    new CompositeBeanPropertySqlParameterSource(
                            person.getPrimaryAddress(),
                            Map.of(
                                    "psn_id", person.getSpin_psn_id().getValue(),
                                    "pref_addr_ind", toInteger(person.getPrimaryAddress().isPref_addr_ind()),
                                    "addr_typ_cd", AddressType.PRIMARY.getCode(),
                                    "audit_update_login", input.getAuditLogin(),
                                    "audit_update_pgm", input.getAuditProgram()
                            )
                    )
            );
            if (updatedRecords != 1) {
                throw new DataMutationException("Person primary address could not be updated for spin_person_id=" + person.getSpin_psn_id());
            }

            insertPsnChangeHistory(
                    PersonChangeHistory.builder()
                            .psn_id(person.getSpin_psn_id().getValue())
                            .psn_data_cd(PRIMARY_POSTAL_ADDRESS.getCode())
                            .chng_from_val(toFixedWidthString(existingRecord))
                            .chng_to_val(toFixedWidthString(person.getPrimaryAddress()))
                            .auditLogin(input.getAuditLogin())
                            .auditProgram(input.getAuditProgram())
                            .build()
            );
        }

        private String toFixedWidthString(PostalAddressInfoInput input) {
            return String.join("",
                    withPadding(40, input.getAddr_line1()),
                    withPadding(40, input.getAddr_line2()),
                    withPadding(40, input.getAddr_line3()),
                    withPadding(40, input.getAddr_line4()),
                    withPadding(40, input.getCity_nm()),
                    withPadding(40, input.getCnty_nm()),
                    withPadding(2, input.getSt_prov_abbr_cd()),
                    withPadding(10, input.getPostcode()),
                    withPadding(2, input.getIso_cntry_cd()),
                    withPadding(1, String.valueOf(toInteger(input.isPref_addr_ind())))
            );
        }

        private void updateAlternateEmailAddress() {
            CommRecord commRecord = new CommRecord(input, person, CommType.ALTERNATE_EMAIL);
            EmailAddressInfoInput emailAddress = person.getAlternateEmailAddress();
            if (emailAddress != null) {
                commRecord
                        .setComm_val(emailAddress.getEmail_addr())
                        .setPref_mthd_ind(emailAddress.isPref_mthd_ind());
            }
            commInsertUpdateDelete(commRecord, emailAddress != null);
        }

        private void updateCellularPhone() {
            CommRecord commRecord = new CommRecord(input, person, CommType.CELL_PHONE);
            PhoneNumberInfoInput phone = person.getCellularPhone();
            if (phone != null) {
                commRecord
                        .setComm_val(phone.getPhone_no())
                        .setComm_mask(phone.getPhone_no_mask())
                        .setExt_pager_pin(phone.getExt_pager_pin())
                        .setPref_mthd_ind(phone.isPref_mthd_ind());
            }
            commInsertUpdateDelete(commRecord, phone != null);
        }

        private void commInsertUpdateDelete(CommRecord updated, boolean commInputProvided) {
            Optional<CommRecord> optExisting = findCommRecord(updated);
            boolean commRecordExists = optExisting.isPresent();

            if (!commRecordExists && commInputProvided) {
                insertComm(updated);
            } else if (commRecordExists && commInputProvided) {
                updateComm(optExisting.get(), updated);
            } else if (commRecordExists && !commInputProvided) {
                deleteComm(updated);
            }
        }

        private void insertComm(CommRecord commRecord) {
            String sql = """
                        INSERT INTO %s (
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
                            :psn_id,
                            :comm_typ_cd,
                            :comm_val,
                            :comm_mask,
                            :ext_pager_pin,
                            :pref_mthd_ind,
                            CURRENT_DATE,
                            CURRENT_TIMESTAMP,
                            :auditLogin,
                            :auditProgram,
                            CURRENT_TIMESTAMP,
                            :auditLogin,
                            :auditProgram
                        )
                    """.formatted(psnCommTable);
            namedJdbcTemplate.update(sql,
                    new CompositeBeanPropertySqlParameterSource(
                            commRecord,
                            Map.of(
                                    "pref_mthd_ind", toInteger(commRecord.isPref_mthd_ind())
                            )
                    )
            );
        }

        private void updateComm(@SuppressWarnings("unused") CommRecord original, CommRecord updated) {
            // Note: X4C supports inserts psn_chng_hist records for CommRecords.
            // But, it only does so if a DriverField is passed to X4C. X4D does NOT pass those DriverFields for anything other than address.

            namedJdbcTemplate.update(
                    """
                            UPDATE %s SET
                                comm_val=:comm_val,
                                comm_mask=:comm_mask,
                                ext_pager_pin=:ext_pager_pin,
                                pref_mthd_ind=:pref_mthd_ind,
                                audit_update_dt=CURRENT_DATE,
                                audit_update_tm=CURRENT_TIMESTAMP,
                                audit_update_login=:auditLogin,
                                audit_update_pgm=:auditProgram,
                                tcv_audit_update_userid=:auditLogin,
                                tcv_audit_update_program=:auditProgram
                            WHERE psn_id=:psn_id AND comm_typ_cd=:comm_typ_cd
                            """.formatted(psnCommTable),
                    new CompositeBeanPropertySqlParameterSource(
                            updated,
                            Map.of(
                                    "pref_mthd_ind", toInteger(updated.isPref_mthd_ind())
                            )
                    )
            );
        }

        private void deleteComm(CommRecord commRecord) {
            String sql = "DELETE FROM %s WHERE psn_id=:psn_id AND comm_typ_cd=:comm_typ_cd".formatted(psnCommTable);
            namedJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(commRecord));
        }

        private Optional<CommRecord> findCommRecord(CommRecord commRecord) {
            BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(commRecord);

            String sql = "SELECT 1 FROM %s WHERE psn_id=:psn_id AND comm_typ_cd=:comm_typ_cd".formatted(psnCommTable);
            SqlRowSet rs = namedJdbcTemplate.queryForRowSet(sql, parameterSource);
            if (!rs.next()) {
                return Optional.empty();
            }

            return Optional.ofNullable(
                    namedJdbcTemplate.queryForObject(
                            "SELECT * FROM %s WHERE psn_id=:psn_id AND comm_typ_cd=:comm_typ_cd".formatted(psnCommTable),
                            parameterSource,
                            new BeanPropertyRowMapper<>(CommRecord.class)
                    )
            );
        }

        private void insertPsnChangeHistory(PersonChangeHistory personChangeHistory) {
            namedJdbcTemplate.update("""
                                INSERT INTO %s (
                                    psn_id,
                                    psn_data_cd,
                                    chng_from_val,
                                    chng_to_val,
                                    chng_dt,
                                    chng_tm,
                                    audit_insert_login,
                                    audit_insert_login_nm,
                                    audit_insert_pgm,
                                    tcv_audit_insert_date,
                                    tcv_audit_insert_userid,
                                    tcv_audit_insert_program
                                ) VALUES (
                                    :psn_id,
                                    :psn_data_cd,
                                    :chng_from_val,
                                    :chng_to_val,
                                    CURRENT_DATE,
                                    CURRENT_TIMESTAMP,
                                    :auditLogin,
                                    :auditLogin,
                                    :auditProgram,
                                    CURRENT_TIMESTAMP,
                                    :auditLogin,
                                    :auditProgram
                                )
                            """.formatted(psnChngHistTable),
                    new BeanPropertySqlParameterSource(personChangeHistory)
            );
        }

        private static String withPadding(int length, String input) {
            return leftPad(normalize(input), length, " ");
        }

        private static String normalize(String input) {
            return trimToEmpty(input).toUpperCase();
        }

        private int toInteger(boolean value) {
            return BooleanUtils.toInteger(value);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    @SuppressWarnings({"unused", "java:S116"})
    private static class CommRecord {
        public CommRecord(LambdaInput<?> input, UpdatePersonInput.Person person, CommType commType) {
            this.psn_id = person.getSpin_psn_id().getValue();
            this.auditLogin = input.getAuditLogin();
            this.auditProgram = input.getAuditProgram();
            this.comm_typ_cd = commType.getCode();
        }

        int psn_id;
        int comm_typ_cd;
        String comm_val;
        String comm_mask;
        String ext_pager_pin;
        boolean pref_mthd_ind;
        String auditProgram;
        String auditLogin;

        public String getComm_mask() {
            return comm_mask == null ? "" : comm_mask;
        }
    }

    @With
    @Getter
    @Setter
    @Builder
    @SuppressWarnings({"unused", "java:S116"})
    private static class PersonChangeHistory {
        int psn_id;
        int psn_data_cd;
        String chng_from_val;
        String chng_to_val;

        String auditProgram;
        String auditLogin;
    }

    @Override
    public List<UpdatePersonResponse> createErrorResponse(UpdatePersonInput input, Exception exception) {
        return input
                .getValue()
                .stream()
                .map(a -> UpdatePersonResponse.builder()
                        .rowCount(0)
                        .success(false)
                        .spin_psn_id(a.getSpin_psn_id())
                        .build())
                .collect(toList());
    }
}
