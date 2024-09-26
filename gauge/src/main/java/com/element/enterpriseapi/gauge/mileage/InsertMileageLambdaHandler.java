package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.common.*;
import com.element.enterpriseapi.exception.InputValidationException;
import io.opentelemetry.api.OpenTelemetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.element.enterpriseapi.common.Constants.DEFAULT_AUDIT_PGM;
import static com.element.enterpriseapi.common.Constants.DEFAULT_USER_LOGIN;
import static com.element.enterpriseapi.gauge.mileage.SQLStatements.*;
import static java.util.Map.entry;

@Slf4j
@Qualifier("CreateMileageReport")
public class InsertMileageLambdaHandler extends AbstractLambdaHandler<MileageInput, MileageResponse> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AssetIdConverter assetIdConverter;
    private final OrgIdConverter orgIdConverter;
    private final String sequenceGeneratorTable;
    private final String mileageReportTable;
    private final String mileageReportDetailsTable;

    public InsertMileageLambdaHandler(
            @Qualifier("edbTxManager") PlatformTransactionManager txManager,
            FQN fqn,
            @Qualifier("edbNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            AssetIdConverter assetIdConverter,
            OrgIdConverter orgIdConverter,
            OpenTelemetry otlp
    ) {
        super(txManager, otlp);
        this.assetIdConverter = assetIdConverter;
        this.orgIdConverter = orgIdConverter;
        this.jdbcTemplate = namedParameterJdbcTemplate;
        this.sequenceGeneratorTable = fqn.resolveSybase("refd1_reference_cur", "sequence_generator");
        this.mileageReportDetailsTable = fqn.resolveSybase("txnd1_driver_mileage_cur", "mileage_report_detail");
        this.mileageReportTable = fqn.resolveSybase("txnd1_driver_mileage_cur", "mileage_report");
    }

    @Override
    public MileageResponse createErrorResponse(MileageInput input, Exception exception) {
        log.error(String.format("Error inserting mileage: %s  cause = %s spin_psn_id = %s", exception.getMessage(), exception.getCause(), input.getValue().getSpin_psn_id()));
        return MileageResponse.builder()
                .success(false)
                .rowCount(0)
                .message(exception.getMessage())
                .build();
    }

    @Override
    public MileageResponse executeInTransaction(MileageInput input) {
        EdbAssetId edbAssetId = assetIdConverter.convert(input.getValue().getSpin_asset_id());
        EdbOrgId edbPersonOrgId = orgIdConverter.convert(input.getValue().getSpin_psn_org_id());
        EdbOrgId edbAssetOrgId = orgIdConverter.convert(input.getValue().getSpin_asset_org_id());
        Assert.notNull(edbAssetId, () -> "EdbAssetId can't be found for SpinAssetId: " + input.getValue().getSpin_asset_id());
        Assert.notNull(edbPersonOrgId, () -> "EdbPersonOrgId can't be found for SpinPersonOrgId: " + input.getValue().getSpin_psn_org_id());
        Assert.notNull(edbAssetOrgId, () -> "EdbAssetOrgId can't be found for SpinAssetOrgId: " + input.getValue().getSpin_asset_org_id());
        input.getValue().setEdb_asset_id(edbAssetId);
        input.getValue().setEdb_psn_org_id(edbPersonOrgId);
        input.getValue().setEdb_asset_org_id(edbAssetOrgId);

        if (entryAlreadyExists(input)) {
            throw new DataMutationException("Mileage Report entry already exists");
        }
        boolean isCA = "CA".equals(input.getValue().getPsn_corp_cd()) && "CA".equals(input.getValue().getAsset_corp_cd());
        String sequenceName = isCA ? "mileage_report_canada" : "mileage_report";

        Integer mileageReportId = getNextMileageReportId(sequenceName);
        updateMileageReportId(input, sequenceName);
        insertMileageReport(input, mileageReportId);
        if (isCA) insertMileageReportDetails(input, mileageReportId);

        return MileageResponse.builder()
                .success(true)
                .spin_mileage_rpt_id(mileageReportId)
                .rowCount(1)
                .build();
    }

    boolean entryAlreadyExists(MileageInput input) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(MILEAGE_ENTRY_EXISTS_SQL.formatted(mileageReportTable),
                new MapSqlParameterSource(
                        Map.ofEntries(
                                entry("psn_id", input.getValue().getSpin_psn_id()),
                                entry("ast_id", input.getValue().getEdb_asset_id().getValue()),
                                entry("period_end_dt", Timestamp.valueOf(input.getValue().getPeriod_end_dt()))
                        )
                )
        );
        return rs.next();
    }

    Integer getNextMileageReportId(String sequenceName) {
        return jdbcTemplate.queryForObject(GET_MILEAGE_SEQUENCE_NEXT_NO.formatted(sequenceGeneratorTable),
                new MapSqlParameterSource(Map.ofEntries(entry("sequence_name", sequenceName))),
                Integer.class);
    }

    void updateMileageReportId(MileageInput input, String sequenceName) {
        jdbcTemplate.update(UPDATE_MILEAGE_SEQUENCE_NO.formatted(sequenceGeneratorTable),
                new MapSqlParameterSource(Map.ofEntries(
                        entry("audit_update_login", Optional.ofNullable(input.getAuditLogin()).orElse(DEFAULT_USER_LOGIN)),
                        entry("audit_update_pgm", Optional.ofNullable(input.getAuditProgram()).orElse(DEFAULT_AUDIT_PGM)),
                        entry("sequence_name", sequenceName)
                )));
    }

    void insertMileageReport(MileageInput input, Integer mileageReportId) {
        MileageInput.Mileage mileage = input.getValue();
        jdbcTemplate.update(INSERT_MILEAGE_SQL.formatted(mileageReportTable),
                new MapSqlParameterSource(Map.ofEntries(
                        entry("mileage_rpt_id", mileageReportId),
                        entry("psn_id", mileage.getSpin_psn_id()),
                        entry("ast_id", mileage.getEdb_asset_id().getValue()),
                        entry("period_end_dt", Timestamp.valueOf(mileage.getPeriod_end_dt())),
                        entry("period_start_dt", Timestamp.valueOf(mileage.getPeriod_start_dt())),
                        entry("bus_mileage_amt", mileage.getBus_mileage_amt()),
                        entry("psn_mileage_amt", mileage.getPsn_mileage_amt()),
                        entry("total_mileage_amt", mileage.getBus_mileage_amt() + mileage.getPsn_mileage_amt()),
                        entry("distance_uom_cd", "FA".equals(mileage.getPsn_corp_cd()) ? "DH" : "DK"),
                        entry("days_in_veh", mileage.getDays_in_veh()),
                        entry("end_odom_reading_amt", mileage.getEnd_odom_reading_amt()),
                        entry("begin_odom_reading_amt", mileage.getBegin_odom_reading_amt()),
                        entry("rpt_period_typ_cd", mileage.getRpt_period_typ_cd()),
                        entry("mileage_rpt_source_cd", mileage.getMileage_rpt_source_cd()),
                        entry("psn_org_id", mileage.getEdb_psn_org_id().getValue()),
                        entry("psn_corp_cd", mileage.getPsn_corp_cd()),
                        entry("psn_cli_no", mileage.getPsn_cli_no()),
                        entry("psn_bkdn", mileage.getPsn_bkdn()),
                        entry("ast_org_id", mileage.getEdb_asset_org_id().getValue()),
                        entry("ast_corp_cd", mileage.getAsset_corp_cd()),
                        entry("ast_cli_no", mileage.getAsset_cli_no()),
                        entry("ast_bkdn", mileage.getAsset_bkdn()),
                        entry("commuter_trips_qty", mileage.getCommuter_trips_qty()),
                        entry("suppress_VER_export_ind", mileage.getSuppress_ver_export_ind()),
                        entry("suppress_IVR_export_ind", mileage.getSuppress_ivr_export_ind()),
                        entry("row_del_ind", 0),
                        entry("audit_insert_login", Optional.ofNullable(input.getAuditLogin()).orElse(DEFAULT_USER_LOGIN)),
                        entry("audit_insert_pgm", Optional.ofNullable(input.getAuditProgram()).orElse(DEFAULT_AUDIT_PGM))
                )));
    }

    void insertMileageReportDetails(MileageInput input, Integer mileageReportId) {
        MapSqlParameterSource[] sqlParameterSources = buildMileageReportDetailsBatch(input, mileageReportId)
                .map(MapSqlParameterSource::new).toList().toArray(new MapSqlParameterSource[0]);

        jdbcTemplate.batchUpdate(INSERT_MILEAGE_DETAIL_SQL.formatted(mileageReportDetailsTable),
                sqlParameterSources);
    }

    private Stream<Map<String, ? extends Serializable>> buildMileageReportDetailsBatch(MileageInput input, Integer mileageReportId) {
        MileageInput.Mileage mileage = input.getValue();
        return mileage.getMileageReportDetail().stream().map(detail ->
                Map.ofEntries(
                        entry("psn_id", mileage.getSpin_psn_id()),
                        entry("mileage_rpt_id", mileageReportId),
                        entry("ast_id", mileage.getEdb_asset_id().getValue()),
                        entry("psn_corp_cd", "CA"),
                        entry("lang_typ_cd", "E"),
                        entry("period_end_dt", Timestamp.valueOf(mileage.getPeriod_end_dt())),
                        entry("period_start_dt", Timestamp.valueOf(mileage.getPeriod_start_dt())),
                        entry("exp_typ_cd", detail.getExp_typ_cd()),
                        entry("uom_typ_cd", detail.getUom_typ_cd()),
                        entry("exp_amt", detail.getExp_amt()),
                        entry("exp_qnty", detail.getExp_qnty()),
                        entry("audit_insert_login", Optional.ofNullable(input.getAuditLogin()).orElse(DEFAULT_USER_LOGIN)),
                        entry("audit_insert_pgm", Optional.ofNullable(input.getAuditProgram()).orElse(DEFAULT_AUDIT_PGM))));
    }


    @Override
    public void validateInput(MileageInput input) throws InputValidationException {
    }
}
