package com.element.enterpriseapi.gauge.odometer;

import com.element.enterpriseapi.common.*;
import io.opentelemetry.api.OpenTelemetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import java.sql.Timestamp;

@Slf4j
@Qualifier("InsertOdometerReading")
public class InsertOdometerReadingLambdaHandler extends AbstractLambdaHandler<OdometerInput, OdometerResponse> {
    private final JdbcTemplate jdbcTemplate;
    private final AssetIdConverter assetIdConverter;
    private final String odometerTable;

    public InsertOdometerReadingLambdaHandler(
            @Qualifier("edbTxManager") PlatformTransactionManager txManager,
            FQN fqn,
            @Qualifier("edbNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            AssetIdConverter assetIdConverter,
            OpenTelemetry otlp
    ) {
        super(txManager, otlp);
        this.odometerTable = fqn.resolveSybase("astd1_asset", "veh_odom");
        this.jdbcTemplate = namedParameterJdbcTemplate.getJdbcTemplate();
        this.assetIdConverter = assetIdConverter;
    }

    @Override
    public void validateInput(OdometerInput input) {
        // temporarily returning OK, should be calling EAPI validation framework
    }

    @Override
    public OdometerResponse createErrorResponse(OdometerInput input, Exception exception) {
        return OdometerResponse.builder()
                .success(false)
                .rowCount(0)
                .message(exception.getMessage())
                .spin_asset_id(input.getValue().getSpin_asset_id())
                .build();
    }

    @Override
    public OdometerResponse executeInTransaction(OdometerInput input) {
        EdbAssetId edbAssetId = assetIdConverter.convert(input.getValue().getSpin_asset_id());
        Assert.notNull(edbAssetId, () -> "EdbAssetId can't be found for SpinAssetId: " + input.getValue().getSpin_asset_id());
        if (entryAlreadyExists(input, edbAssetId)) {
            throw new DataMutationException("Odometer entry already exists");
        }

        insertOdometerRecord(input, edbAssetId);

        return OdometerResponse.builder()
                .success(true)
                .spin_asset_id(input.getValue().getSpin_asset_id())
                .rowCount(1)
                .build();
    }

    boolean entryAlreadyExists(OdometerInput input, EdbAssetId edbAssetId) {
        OdometerInput.Odometer odometer = input.getValue();
        String sql = """
                 SELECT 1 FROM %s\s
                 WHERE ast_id = ?
                 and odom_rdng_dt = ?
                 and odom_rdng_typ_cd = ?
                 and odom_rdng_amt = ?
                \s""".formatted(odometerTable);

        var bindValues = new Object[]{
                edbAssetId.getValue(),
                Timestamp.valueOf(odometer.getOdom_reading_dt()),
                odometer.getOdom_reading_typ_cd(),
                odometer.getOdom_reading_amt()
        };

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, bindValues);
        return rs.next();
    }

    private void insertOdometerRecord(OdometerInput input, EdbAssetId edbAssetId) {
        OdometerInput.Odometer odometer = input.getValue();
        String sql = """
                        INSERT INTO %s\s
                                (ast_id, odom_rdng_dt, odom_rdng_typ_cd, odom_rdng_amt, audit_insert_pgm, audit_insert_login)
                        VALUES
                                (?, ?, ?, ?, ?, ?)
                """.formatted(odometerTable);
        var bindValues = new Object[]{
                edbAssetId.getValue(),
                Timestamp.valueOf(odometer.getOdom_reading_dt()),
                odometer.getOdom_reading_typ_cd(),
                odometer.getOdom_reading_amt(),
                input.getAuditProgram(),
                input.getAuditLogin()
        };
        jdbcTemplate.update(sql, bindValues);
    }

}
