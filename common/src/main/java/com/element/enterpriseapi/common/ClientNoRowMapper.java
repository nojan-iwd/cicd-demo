package com.element.enterpriseapi.common;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientNoRowMapper implements RowMapper<ClientNoXref> {
    @Override
    public ClientNoXref mapRow(ResultSet rs, int rowNum) throws SQLException {
        String clientNo = rs.getString("cli_no");
        String corpCode = rs.getString("corp_cd");
        return ClientNoXref.builder()
                .cli_no(new ClientNo(clientNo))
                .corp_cd(corpCode)
                .build();
    }
}
