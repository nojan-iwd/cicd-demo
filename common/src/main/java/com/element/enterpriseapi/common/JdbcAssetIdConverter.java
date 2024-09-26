package com.element.enterpriseapi.common;

import com.element.enterpriseapi.exception.EnterpriseApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

public class JdbcAssetIdConverter implements AssetIdConverter {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String tableName;

    public JdbcAssetIdConverter(FQN fqn,
                                @Qualifier("edbNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.tableName = fqn.resolveSybase("cord1_asset_cur", "dan_xref");
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public EdbAssetId convert(SpinAssetId spinAssetId) {
        try {
            String sql = "SELECT edb_asset_id FROM " + tableName + " WHERE SPIN_asset_id = :SPIN_asset_id AND ast_del_from_src_ind = 'N'";
            Integer edbAssetId = namedParameterJdbcTemplate.queryForObject(sql,
                    new MapSqlParameterSource(Map.of("SPIN_asset_id", spinAssetId.getValue())),
                    Integer.class);
            return new EdbAssetId(edbAssetId);
        } catch (EmptyResultDataAccessException e) {
            throw new EnterpriseApiException("EdbAssetId not found for SpinAssetId: " + spinAssetId);
        } catch (Exception e) {
            throw new EnterpriseApiException(e);
        }
    }
}
