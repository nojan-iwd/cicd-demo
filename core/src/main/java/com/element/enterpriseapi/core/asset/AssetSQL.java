package com.element.enterpriseapi.core.asset;

public class AssetSQL {
    public static final String UPDATE_ASSET_SQL = """
            UPDATE %s
            SET tcv_audit_update_date = CURRENT_DATE,
                tcv_audit_update_userid = :tcv_audit_update_userid,
                tcv_audit_update_program = :tcv_audit_update_program,
                %s
            WHERE dlr_asgn_no = :dlr_asgn_no
            """;

    public static final String UPDATE_ASSET_DRIVER_INPUT_SQL = """
            UPDATE %1$s.%2$s d
            SET tcv_audit_update_date = CURRENT_DATE,
                tcv_audit_update_userid = :tcv_audit_update_userid,
                tcv_audit_update_program = :tcv_audit_update_program,
                %3$s
            FROM %1$s.ast_id_mapping aim
            INNER JOIN %1$s.mast_unit mu
            ON aim.corp_cd = mu.corp_cd
            AND aim.dan = mu.dlr_asgn_no
            WHERE aim.spin_asset_id=%4$d
            AND mu.corp_cd = d.corp_cd
            AND d.client_no = :unmodified_client_no
            AND d.unit_no = :unmodified_unit_no;
            """;

    public static final String UPDATE_SHARED_INVENTORY_INPUT_SQL = """
            UPDATE %1$s.%2$s d
            SET tcv_audit_update_dt = CURRENT_DATE,
                tcv_audit_update_login = :tcv_audit_update_userid,
                tcv_audit_update_pgm = :tcv_audit_update_program,
                %3$s
            FROM %1$s.ast_id_mapping aim
            INNER JOIN %1$s.mast_unit mu
            ON aim.corp_cd = mu.corp_cd
            AND aim.dan = mu.dlr_asgn_no
            WHERE aim.spin_asset_id=%4$d
            AND d.client_no = :unmodified_client_no
            AND d.unit_no = :unmodified_unit_no;
            """;
}
