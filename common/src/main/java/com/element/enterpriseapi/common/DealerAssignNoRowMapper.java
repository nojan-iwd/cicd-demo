package com.element.enterpriseapi.common;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DealerAssignNoRowMapper implements RowMapper<DanXref> {
    @Override
    public DanXref mapRow(ResultSet rs, int rowNum) throws SQLException {
        String dan = rs.getString("dan");
        String corpCd = rs.getString("corp_cd");
        String unmodifiedUnitNo = rs.getString("unit_no");
        String unmodifiedClientNo = rs.getString("client_no");
        return DanXref.builder()
                .dan(new DealerAssignNo(dan))
                .corp_cd(corpCd)
                .unmodified_client_no(unmodifiedClientNo)
                .unmodified_unit_no(unmodifiedUnitNo)
                .build();
    }
}
