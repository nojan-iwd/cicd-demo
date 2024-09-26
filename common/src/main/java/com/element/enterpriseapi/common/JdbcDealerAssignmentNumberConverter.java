package com.element.enterpriseapi.common;

import com.element.enterpriseapi.exception.EnterpriseApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

public class JdbcDealerAssignmentNumberConverter implements DealerAssignmentNumberConverter {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String tableName;
    private final String tableName2;

    public JdbcDealerAssignmentNumberConverter(FQN fqn,
                                               @Qualifier("mainframeNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.tableName = fqn.resolvePostgres("sysusr", "ast_id_mapping");
        this.tableName2 = fqn.resolvePostgres("sysusr", "mast_unit");
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public DanXref convert(SpinAssetId spinAssetId) {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT aim.dan, aim.corp_cd, mu.client_no, mu.unit_no FROM %s aim LEFT JOIN %s mu ON aim.corp_cd = mu.corp_cd and aim.dan = mu.dlr_asgn_no WHERE aim.spin_asset_id = :spin_asset_id AND aim.ast_del_from_src_ind = 'N'".formatted(tableName, tableName2),
                    new MapSqlParameterSource(Map.of("spin_asset_id", spinAssetId.getValue())),
                    new DealerAssignNoRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            throw new EnterpriseApiException("DAN (Dealer Assign No) not found for SpinAssetId: " + spinAssetId);
        } catch (Exception e) {
            throw new EnterpriseApiException(e);
        }
    }


}
