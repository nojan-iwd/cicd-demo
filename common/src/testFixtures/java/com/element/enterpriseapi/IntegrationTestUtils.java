package com.element.enterpriseapi;

import com.element.enterpriseapi.common.EdbAssetId;
import com.element.enterpriseapi.common.EdbOrgId;
import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.common.SpinOrgId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"SqlSourceToSinkFlow"})
@Getter
@RequiredArgsConstructor
public class IntegrationTestUtils {
    private final JdbcTemplate jdbcTemplate;

    public void insertAssetIdMapping(SpinAssetId spinAssetId, EdbAssetId edbAssetId) {
        jdbcTemplate.update("INSERT INTO cord1_asset_cur.dan_xref (edb_asset_id, spin_asset_id, ast_del_from_src_ind) VALUES(?, ?, 'N')", edbAssetId.getValue(), spinAssetId.getValue());
    }

    public void insertOrgIdMapping(SpinOrgId spinOrgId, EdbOrgId edbOrgId) {
        jdbcTemplate.update("INSERT INTO cord1_client_cur.cli_no_xref (edb_org_id, spin_org_id, corp_cd, cli_no, src_extract_dt, src_add_dt, cli_del_from_src_ind) VALUES(?, ?, 'CA', '0008', getdate(), getdate(), 'N')",
                edbOrgId.getValue(), spinOrgId.getValue());
    }


    public Map<String, Object> readRow(String tableName, String where, Object... args) {
        var sql = "SELECT * FROM " + tableName + " WHERE " + where;
        return jdbcTemplate.queryForMap(sql, args);
    }

    public List<Map<String, Object>> readRows(String tableName, List<String> columns, String where, Object... args) {
        String cols = columns.isEmpty() ? "*" : String.join(", ", columns);
        var sql = "SELECT " + cols + " FROM " + tableName + " WHERE " + where;
        return jdbcTemplate.queryForList(sql, args);
    }

    public Integer countRows(String tableName, String where, Object... args) {
        var sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + where;
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

    public void executeSQL(String sql) {
        jdbcTemplate.execute(sql);
    }

}
