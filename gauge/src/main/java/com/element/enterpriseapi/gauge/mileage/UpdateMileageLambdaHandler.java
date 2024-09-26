package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.common.*;
import com.element.enterpriseapi.exception.InputValidationException;
import io.opentelemetry.api.OpenTelemetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.element.enterpriseapi.common.Constants.DEFAULT_AUDIT_PGM;
import static com.element.enterpriseapi.common.Constants.DEFAULT_USER_LOGIN;
import static com.element.enterpriseapi.gauge.mileage.SQLStatements.*;
import static java.util.Map.entry;

@Slf4j
@Qualifier("UpdateMileageReport")
public class UpdateMileageLambdaHandler extends AbstractLambdaHandler<MileageInput, MileageResponse> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AssetIdConverter assetIdConverter;
    private final OrgIdConverter orgIdConverter;
    public final String mileageReportTable;
    public final String mileageReportDetailsTable;

    public UpdateMileageLambdaHandler(
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
        this.mileageReportDetailsTable = fqn.resolveSybase("txnd1_driver_mileage_cur", "mileage_report_detail");
        this.mileageReportTable = fqn.resolveSybase("txnd1_driver_mileage_cur", "mileage_report");
    }

    @Override
    public MileageResponse executeInTransaction(MileageInput input) {
        EdbAssetId edbAssetId = assetIdConverter.convert(input.getValue().getSpin_asset_id());
        Assert.notNull(edbAssetId, () -> "EdbAssetId can't be found for SpinAssetId: " + input.getValue().getSpin_asset_id());
        input.getValue().setEdb_asset_id(edbAssetId);

        SpinOrgId spinPersonOrgId = input.getValue().getSpin_psn_org_id();
        if (spinPersonOrgId != null) {
            EdbOrgId edbPersonOrgId = orgIdConverter.convert(spinPersonOrgId);
            Assert.notNull(edbPersonOrgId, () -> "EdbPersonOrgId can't be found for SpinPersonOrgId: " + input.getValue().getSpin_psn_org_id());
            input.getValue().setEdb_psn_org_id(edbPersonOrgId);

        }
        SpinOrgId spinAssetOrgId = input.getValue().getSpin_asset_org_id();
        if (spinAssetOrgId != null) {
            EdbOrgId edbAssetOrgId = orgIdConverter.convert(spinAssetOrgId);
            Assert.notNull(edbAssetOrgId, () -> "EdbAssetOrgId can't be found for SpinAssetOrgId: " + input.getValue().getSpin_asset_org_id());
            input.getValue().setEdb_asset_org_id(edbAssetOrgId);
        }
        updateMileageReport(input);
        updateMileageReportDetail(input);

        return MileageResponse.builder()
                .success(true)
                .spin_mileage_rpt_id(input.getValue().getSpin_mileage_rpt_id())
                .rowCount(1)
                .build();
    }

    void updateMileageReport(MileageInput input) {
        int rowCount = jdbcTemplate.update(
                UPDATE_MILEAGE_SQL.formatted(mileageReportTable, buildUpdateMileageColumnsStr(input)),
                new CompositeBeanPropertySqlParameterSource(
                        input.getValue(),
                        Map.ofEntries(
                                entry("audit_update_login", Optional.ofNullable(input.getAuditLogin()).orElse(DEFAULT_USER_LOGIN)),
                                entry("audit_update_pgm", Optional.ofNullable(input.getAuditProgram()).orElse(DEFAULT_AUDIT_PGM))
                        )
                )
        );
        if (rowCount == 0) {
            throw new DataMutationException("Mileage Report entry doesn't exist");
        }
    }

    void updateMileageReportDetail(MileageInput input) {
        MapSqlParameterSource[] sqlParameterSources = buildMileageReportDetailsBatch(input)
                .map(MapSqlParameterSource::new).toList().toArray(new MapSqlParameterSource[0]);
        jdbcTemplate.batchUpdate(UPDATE_MILEAGE_DETAIL_SQL.formatted(mileageReportDetailsTable), sqlParameterSources);
    }

    private Stream<Map<String, ? extends Serializable>> buildMileageReportDetailsBatch(MileageInput input) {
        MileageInput.Mileage mileage = input.getValue();
        return mileage.getMileageReportDetail().stream().map(detail ->
                Map.ofEntries(
                        entry("spin_mileage_rpt_id", input.getValue().getSpin_mileage_rpt_id()),
                        entry("exp_typ_cd", detail.getExp_typ_cd()),
                        entry("uom_typ_cd", detail.getUom_typ_cd()),
                        entry("exp_amt", detail.getExp_amt()),
                        entry("exp_qnty", detail.getExp_qnty()),
                        entry("audit_update_login", Optional.ofNullable(input.getAuditLogin()).orElse(DEFAULT_USER_LOGIN)),
                        entry("audit_update_pgm", Optional.ofNullable(input.getAuditProgram()).orElse(DEFAULT_AUDIT_PGM))));
    }

    @Override
    public MileageResponse createErrorResponse(MileageInput input, Exception exception) {
        log.error(String.format("Error updating mileage: %s  cause = %s spin_psn_id = %s",
                exception.getMessage(), exception.getCause(), input.getValue().getSpin_psn_id()));
        return MileageResponse.builder()
                .success(false)
                .rowCount(0)
                .message(exception.getMessage())
                .spin_mileage_rpt_id(input.getValue().getSpin_mileage_rpt_id())
                .build();
    }

    @Override
    public void validateInput(MileageInput input) throws InputValidationException {

    }
}
