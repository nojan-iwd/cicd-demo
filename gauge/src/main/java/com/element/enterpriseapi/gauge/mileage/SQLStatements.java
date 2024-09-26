package com.element.enterpriseapi.gauge.mileage;

import java.util.ArrayList;
import java.util.List;

class SQLStatements {

    private SQLStatements() {
    }

    public static final String MILEAGE_ENTRY_EXISTS_SQL = """
             SELECT 1 FROM %s mr
               WHERE mr.psn_id = :psn_id
                     AND mr.ast_id = :ast_id
                     AND mr.period_end_dt = :period_end_dt
                     AND mr.row_del_ind = 0
            """;

    public static final String INSERT_MILEAGE_SQL = """
             INSERT INTO %s (
                  mileage_rpt_id,
                  psn_id,
                  ast_id,
                  period_end_dt,
                  period_start_dt,
                  mileage_rpt_entry_dt,
                  bus_mileage_amt,
                  psn_mileage_amt,
                  total_mileage_amt,
                  distance_uom_cd,
                  days_in_veh,
                  end_odom_reading_amt,
                  begin_odom_reading_amt,
                  rpt_period_typ_cd,
                  mileage_rpt_source_cd,
                  psn_org_id,
                  psn_corp_cd,
                  psn_cli_no,
                  psn_bkdn,
                  ast_org_id,
                  ast_corp_cd,
                  ast_cli_no,
                  ast_bkdn,
                  commuter_trips_qty,
                  suppress_VER_export_ind,
                  suppress_IVR_export_ind,
                  row_del_ind,
                  audit_insert_dt,
                  audit_insert_login,
                  audit_insert_pgm
               ) VALUES (
                  :mileage_rpt_id,
                  :psn_id,
                  :ast_id,
                  :period_end_dt,
                  :period_start_dt,
                  getdate(),
                  :bus_mileage_amt,
                  :psn_mileage_amt,
                  :total_mileage_amt,
                  :distance_uom_cd,
                  :days_in_veh,
                  :end_odom_reading_amt,
                  :begin_odom_reading_amt,
                  :rpt_period_typ_cd,
                  :mileage_rpt_source_cd,
                  :psn_org_id,
                  :psn_corp_cd,
                  :psn_cli_no,
                  :psn_bkdn,
                  :ast_org_id,
                  :ast_corp_cd,
                  :ast_cli_no,
                  :ast_bkdn,
                  :commuter_trips_qty,
                  :suppress_VER_export_ind,
                  :suppress_IVR_export_ind,
                  0,
                  getdate(),
                  :audit_insert_login,
                  :audit_insert_pgm
               )
            """;

    public static final String UPDATE_MILEAGE_SQL = """
            UPDATE %s
            SET   audit_update_dt = getdate(),
                  audit_update_login = :audit_update_login,
                  audit_update_pgm = :audit_update_pgm,
                  %s
            WHERE mileage_rpt_id = :spin_mileage_rpt_id AND row_del_ind = 0
            """;

    public static String buildUpdateMileageColumnsStr(MileageInput input) {
        MileageInput.Mileage mileage = input.getValue();
        List<String> columns = new ArrayList<>();
        if (mileage.getPeriod_end_dt() != null) columns.add("period_end_dt = :period_end_dt");
        if (mileage.getPeriod_start_dt() != null) columns.add("period_start_dt = :period_start_dt");
        if (mileage.getBus_mileage_amt() != null) columns.add("bus_mileage_amt = :bus_mileage_amt");
        if (mileage.getPsn_mileage_amt() != null) columns.add("psn_mileage_amt = :psn_mileage_amt");
        if (mileage.getDays_in_veh() != null) columns.add("days_in_veh = :days_in_veh");
        if (mileage.getMileage_rpt_source_cd() != null) columns.add("mileage_rpt_source_cd = :mileage_rpt_source_cd");
        if (mileage.getEdb_psn_org_id() != null) columns.add("psn_org_id = " + mileage.getEdb_psn_org_id().getValue());
        if (mileage.getPsn_corp_cd() != null) columns.add("psn_corp_cd = :psn_corp_cd");
        if (mileage.getPsn_cli_no() != null) columns.add("psn_cli_no = :psn_cli_no");
        if (mileage.getPsn_bkdn() != null) columns.add("psn_bkdn = :psn_bkdn");
        if (mileage.getEdb_asset_org_id() != null)
            columns.add("ast_org_id = " + mileage.getEdb_asset_org_id().getValue());
        if (mileage.getAsset_corp_cd() != null) columns.add("ast_corp_cd = :asset_corp_cd");
        if (mileage.getAsset_cli_no() != null) columns.add("ast_cli_no = :asset_cli_no");
        if (mileage.getAsset_bkdn() != null) columns.add("ast_bkdn = :asset_bkdn");
        if (mileage.getSuppress_ver_export_ind() != null)
            columns.add("suppress_ver_export_ind = :suppress_ver_export_ind");
        if (mileage.getSuppress_ivr_export_ind() != null)
            columns.add("suppress_ivr_export_ind = :suppress_ivr_export_ind");
        if (mileage.getEnd_odom_reading_amt() != null)
            columns.add("end_odom_reading_amt = :end_odom_reading_amt");
        if (mileage.getBegin_odom_reading_amt() != null)
            columns.add("begin_odom_reading_amt = :begin_odom_reading_amt");
        if (mileage.getRpt_period_typ_cd() != null)
            columns.add("rpt_period_typ_cd = :rpt_period_typ_cd");
        if (mileage.getBus_mileage_amt() != null && mileage.getPsn_mileage_amt() != null)
            columns.add("total_mileage_amt = " + (mileage.getPsn_mileage_amt() + mileage.getBus_mileage_amt()));
        columns.add("distance_uom_cd = '" + ("FA".equals(mileage.getPsn_corp_cd()) ? "DH" : "DK" + "'"));

        return String.join(", ", columns);
    }

    public static final String INSERT_MILEAGE_DETAIL_SQL = """
            INSERT INTO %s (
                psn_id,
                mileage_rpt_id,
                ast_id,
                exp_typ_cd,
                psn_corp_cd,
                period_end_dt,
                period_start_dt,
                uom_typ_cd,
                lang_typ_cd,
                exp_amt,
                exp_qnty,
                audit_insert_dt,
                audit_insert_login,
                audit_insert_pgm
            ) VALUES (
                :psn_id,
                :mileage_rpt_id,
                :ast_id,
                :exp_typ_cd,
                :psn_corp_cd,
                :period_end_dt,
                :period_start_dt,
                :uom_typ_cd,
                :lang_typ_cd,
                :exp_amt,
                :exp_qnty,
                getdate(),
                :audit_insert_login,
                :audit_insert_pgm
            )
            """;

    public static final String UPDATE_MILEAGE_DETAIL_SQL = """
            UPDATE %s
            SET    uom_typ_cd = :uom_typ_cd,
                   exp_amt = :exp_amt,
                   exp_qnty = :exp_qnty,
                   audit_update_dt = getdate(),
                   audit_update_login = :audit_update_login,
                   audit_update_pgm = :audit_update_pgm
            WHERE mileage_rpt_id = :spin_mileage_rpt_id AND exp_typ_cd = :exp_typ_cd   
             """;

    public static final String GET_MILEAGE_SEQUENCE_NEXT_NO = """
            SELECT (last_seq_no + 1)
            FROM %s
            WHERE seq_nm = :sequence_name
            """;

    public static final String UPDATE_MILEAGE_SEQUENCE_NO = """
            UPDATE %s
            SET last_seq_no = last_seq_no + 1,
                audit_update_dt = getdate(),
                audit_update_login = :audit_update_login,
                audit_update_pgm = :audit_update_pgm
            WHERE seq_nm = :sequence_name
            """;
}