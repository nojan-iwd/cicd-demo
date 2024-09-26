package com.element.enterpriseapi.common;

import com.element.enterpriseapi.exception.EnterpriseApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

public class JdbcOrgIdConverter implements OrgIdConverter {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParamaterJdbcTemplate2;
    private final String tableName;
    private final String tableName2;

    public JdbcOrgIdConverter(FQN fqn,
                              @Autowired(required = false) @Qualifier("edbNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              @Autowired(required = false) @Qualifier("mainframeNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParamaterJdbcTemplate2) {
        this.tableName = fqn.resolveSybase("cord1_client_cur", "cli_no_xref");
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.tableName2 = fqn.resolvePostgres("sysusr", "org_id_mapping");
        this.namedParamaterJdbcTemplate2 = namedParamaterJdbcTemplate2;
    }

    @Override
    public EdbOrgId convert(SpinOrgId spinOrgId) {
        try {
            String sql = "SELECT edb_org_id FROM " + tableName + " WHERE SPIN_org_id = :SPIN_org_id AND cli_del_from_src_ind = 'N'";
            Integer edbOrgId = namedParameterJdbcTemplate.queryForObject(sql,
                    new MapSqlParameterSource(Map.of("SPIN_org_id", spinOrgId.getValue())),
                    Integer.class);
            return new EdbOrgId(edbOrgId);
        } catch (EmptyResultDataAccessException e) {
            throw new EnterpriseApiException("EdbOrgId not found for SpinOrgId: " + spinOrgId);
        } catch (Exception e) {
            throw new EnterpriseApiException(e);
        }
    }

    @Override
    public ClientNoXref resolve(SpinOrgId spinOrgId) {
        try {
            String sql = "SELECT cli_no, corp_cd FROM " + tableName2 + " WHERE spin_org_id = :spin_org_id AND cli_del_from_src_ind = 'N'";
            return namedParamaterJdbcTemplate2.queryForObject(
                    sql,
                    new MapSqlParameterSource(Map.of("spin_org_id", spinOrgId.getValue())),
                    new ClientNoRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            throw new EnterpriseApiException("ClientNo not resolved for SpinOrgId: " + spinOrgId);
        } catch (Exception e) {
            throw new EnterpriseApiException(e);
        }
    }
}
