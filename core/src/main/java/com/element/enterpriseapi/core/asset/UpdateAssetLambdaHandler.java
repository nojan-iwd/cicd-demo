package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.common.*;
import com.element.enterpriseapi.exception.InputValidationException;
import com.element.enterpriseapi.lambda.LambdaInput;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.element.enterpriseapi.common.Constants.DEFAULT_AUDIT_PGM;
import static com.element.enterpriseapi.common.Constants.DEFAULT_USER_LOGIN;
import static com.element.enterpriseapi.core.asset.AssetSQL.*;
import static java.util.stream.Collectors.toList;

@Slf4j
@Qualifier("UpdateAsset")
public class UpdateAssetLambdaHandler extends AbstractLambdaHandler<UpdateAssetInput, List<UpdateAssetResponse>> {

    private final NamedParameterJdbcTemplate mainframeNamedJdbcTemplate;

    private final DealerAssignmentNumberConverter dealerAssignmentNumberConverter;
    private final OrgIdConverter orgIdConverter;
    private final DbFieldGroupAspect dbFieldGroupAspect;

    public UpdateAssetLambdaHandler(
            @Qualifier("mainframeTxManager") PlatformTransactionManager txManager,
            @Qualifier("mainframeNamedParameterJdbcTemplate") NamedParameterJdbcTemplate mainframeNamedJdbcTemplate,
            OpenTelemetry otlp,
            DealerAssignmentNumberConverter dealerAssignmentNumberConverter,
            DbFieldGroupAspect dbFieldGroupAspect,
            OrgIdConverter orgIdConverter
    ) {
        super(txManager, otlp);
        this.dbFieldGroupAspect = dbFieldGroupAspect;
        this.mainframeNamedJdbcTemplate = mainframeNamedJdbcTemplate;
        this.dealerAssignmentNumberConverter = dealerAssignmentNumberConverter;
        this.orgIdConverter = orgIdConverter;
    }

    @Override
    public void validateInput(UpdateAssetInput input) throws InputValidationException {
    }

    @Override
    public List<UpdateAssetResponse> executeInTransaction(UpdateAssetInput input) {
        return input
                .getValue()
                .stream()
                .map(e -> new UpdateAssetLambdaHandler.UpdateAssetEntryProcessor(input, e).process())
                .toList();
    }

    @RequiredArgsConstructor
    private class UpdateAssetEntryProcessor {
        private final LambdaInput<?> input;
        private final UpdateAssetInput.Asset asset;
        private final Map<String, String> commonItemsMap = new HashMap<>();

        private String schema;
        private DanXref dan;
        private ClientNoXref clientNo;

        public UpdateAssetResponse process() {
            dan = dealerAssignmentNumberConverter.convert(asset.getKey().getSpin_asset_id());
            if (asset.getData().getSpin_org_id() != null) {
                clientNo = orgIdConverter.resolve(new SpinOrgId(asset.getData().getSpin_org_id()));
                if (!Objects.equals(dan.getCorp_cd(), clientNo.getCorp_cd())) {
                    throw new DataMutationException("Asset details could not be updated because it leads to corp code mutation with spin_org_id=%s".formatted(asset.getData().getSpin_org_id()));
                }
                if (clientNo.getCli_no() != null) {
                    String clientNoString = clientNo.getCli_no().toString();
                    AssetInput data = asset.getData();
                    data.setClient_no_mu(clientNoString);
                    data.setClient_no_muf(clientNoString);
                    data.setDriverInfo( data.getDriverInfo() != null ?
                            data.getDriverInfo().toBuilder()
                                    .client_no_drvr(clientNoString)
                                    .build() :
                            AssetDriverInfo.builder()
                                    .client_no_drvr(clientNoString)
                                    .build());
                }
            }
            populateCommonItems();
            switch (dan.country()) {
                case Country.US -> schema = "sysusr";
                case Country.CA -> schema = "sysusr_ca";
                case Country.MX -> schema = "sysusr_mx";
            }
            for (String tableName : List.of("mast_unit", "mast_unit_fin", "mast_unit_ext")) {
                updateAssetTable(tableName);
            }

            for (String tableName : List.of("driver", "shared_inv")) {
                updateAssetDriverAddress(tableName);
            }

            return UpdateAssetResponse.builder()
                    .success(true)
                    .rowCount(1)
                    .spin_asset_id(asset.getKey().getSpin_asset_id())
                    .build();
        }

        private void populateCommonItems() {
            commonItemsMap.put("tcv_audit_update_userid", Optional.ofNullable(input.getAuditLogin()).orElse(DEFAULT_USER_LOGIN));

            commonItemsMap.put("tcv_audit_update_program", Optional.ofNullable(input.getAuditProgram()).orElse(DEFAULT_AUDIT_PGM));

            if (Strings.isNotBlank(asset.getData().getUnit_no())) {
                commonItemsMap.put("unit_no", asset.getData().getUnit_no());
            }

            commonItemsMap.put("dlr_asgn_no", dan.getDan().getValue());
            commonItemsMap.put("unmodified_client_no", dan.getUnmodified_client_no());
            commonItemsMap.put("unmodified_unit_no", dan.getUnmodified_unit_no());
        }

        private void updateAssetTable(String table) {
            DbFieldGroupResult updateData = dbFieldGroupAspect.processObject(asset.getData(), table);
            if (!updateData.hasDataChanges()) {
                log.info("No asset changes found for table: %s".formatted(table));
                return;
            }
            String sql = UPDATE_ASSET_SQL.formatted("%s.%s".formatted(schema, table), updateData.getSetStatement());
            updateData.getValues().putAll(commonItemsMap);
            int updatedRecords = mainframeNamedJdbcTemplate.update(sql, updateData.getValues());

            if (updatedRecords != 1) {
                throw new DataMutationException("Asset details could not be updated in table %s for spin_asset_id=%d dealer_asgn_no=%s".formatted(table, asset.getKey().getSpin_asset_id().getValue(), dan.getDan().getValue()));
            }
        }

        private void updateAssetDriverAddress(String table) {
            if (asset.getData().getDriverInfo() != null) {
                DbFieldGroupResult updateData = dbFieldGroupAspect.processObject(asset.getData().getDriverInfo(), table);
                Map<String, Object> allData = new HashMap<>(updateData.getValues());
                String setStatement = updateData.getSetStatement();
                if (Objects.equals(table, "driver") && asset.getData().getDriverInfo().getDrvrAddr() != null) {
                    DbFieldGroupResult addressData = dbFieldGroupAspect.processObject(asset.getData().getDriverInfo().getDrvrAddr(), table);
                    if (addressData.hasDataChanges()) {
                        allData.putAll(addressData.getValues());
                        setStatement = Stream.of(updateData.getSetStatement(), addressData.getSetStatement())
                                .filter(Strings::isNotBlank)
                                .collect(Collectors.joining(", "));
                    }
                }
                if (allData.isEmpty()) {
                    log.info("No asset changes found for table: {}", table);
                    return;
                }
                allData.putAll(commonItemsMap);

                String sql = Objects.equals(table, "driver") ?
                        UPDATE_ASSET_DRIVER_INPUT_SQL.formatted(schema, table, setStatement, asset.getKey().getSpin_asset_id().getValue()) :
                        UPDATE_SHARED_INVENTORY_INPUT_SQL.formatted(schema, table, setStatement, asset.getKey().getSpin_asset_id().getValue());
                int updatedRecords = mainframeNamedJdbcTemplate.update(sql, allData);
                log.info("Updated asset table: %s, sql: %s, alldata:%s".formatted(table, sql, allData));
                if (updatedRecords != 1) {
                    throw new DataMutationException("Asset details could not be updated in table %s for spin_asset_id=%d".formatted(table, asset.getKey().getSpin_asset_id().getValue()));
                }
            }
        }
    }

    @Override
    public List<UpdateAssetResponse> createErrorResponse(UpdateAssetInput input, Exception exception) {
        return input
                .getValue()
                .stream()
                .map(a -> UpdateAssetResponse.builder()
                        .rowCount(0)
                        .success(false)
                        .spin_asset_id(a.getKey().getSpin_asset_id())
                        .message(exception.getMessage())
                        .build())
                .collect(toList());
    }
}
