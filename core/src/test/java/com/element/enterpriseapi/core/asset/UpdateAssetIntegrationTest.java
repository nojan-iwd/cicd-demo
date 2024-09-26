package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.BaseIntegrationTest;
import com.element.enterpriseapi.IntegrationTestUtils;
import com.element.enterpriseapi.common.ClientNo;
import com.element.enterpriseapi.common.DealerAssignNo;
import com.element.enterpriseapi.common.SpinAssetId;
import com.element.enterpriseapi.core.CoreConfiguration;
import com.element.enterpriseapi.core.common.PostalAddressInfoInput;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
@Import(CoreConfiguration.class)
@Transactional(transactionManager = "mainframeTxManager")
@Rollback
public class UpdateAssetIntegrationTest extends BaseIntegrationTest implements WithAssertions {
    private static final SpinAssetId SPIN_ASSET_ID = new SpinAssetId(12345);
    private static final DealerAssignNo DEALER_ASSIGN_NO = new DealerAssignNo("DAN100");
    private static final ClientNo PREVIOUS_CLIENT_NO = new ClientNo("CLI001");

    private final UpdateAssetLambdaHandler lambdaHandler;
    private final IntegrationTestUtils testUtils;

    public UpdateAssetIntegrationTest(@Autowired UpdateAssetLambdaHandler lambdaHandler,
                                      @Autowired @Qualifier("mainframeNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.lambdaHandler = lambdaHandler;
        this.testUtils = new IntegrationTestUtils(namedJdbcTemplate.getJdbcTemplate());
    }

    @Test
    void updatesAssetData() {
        insertDAN();
        insertAssetData();

        UpdateAssetInput input = createUpdateAssetInput();
        UpdateAssetInput.Asset entry = input.getValue().getFirst();
        var response = lambdaHandler.apply(input).getFirst();

        assertThat(response.getSuccess()).overridingErrorMessage(response::getMessage).isTrue();
        assertMastUnitUpdated(input, entry);
        assertMastUnitFinUpdated(input, entry);
        assertMastUnitExtUpdated(input, entry);
        assertAssetAddressUpdated(input, entry);
    }

    private void assertAssetAddressUpdated(UpdateAssetInput input, UpdateAssetInput.Asset entry) {
        var row = testUtils.readRow("sysusr.driver", "unit_no=? and client_no=?", "07392", "CLI003");
        assertThat(row.get("driv_addr1").toString().trim()).isEqualTo(entry.getData().getDriverInfo().getDrvrAddr().getAddr_line1());
        assertThat(row.get("driv_addr2").toString().trim()).isEqualTo(entry.getData().getDriverInfo().getDrvrAddr().getAddr_line2());
        assertThat(row.get("driv_city").toString().trim()).isEqualTo(entry.getData().getDriverInfo().getDrvrAddr().getCity_nm());
        assertThat(row.get("driv_cnty").toString().trim()).isEqualTo(entry.getData().getDriverInfo().getDrvrAddr().getCnty_nm());
        assertThat(row.get("driv_state").toString().trim()).isEqualTo(entry.getData().getDriverInfo().getDrvrAddr().getSt_prov_abbr_cd());
        assertThat(row.get("driv_zipcode").toString().trim()).isEqualTo(entry.getData().getDriverInfo().getDrvrAddr().getPostcode());
    }

    private void assertMastUnitUpdated(UpdateAssetInput input, UpdateAssetInput.Asset entry) {
        var row = testUtils.readRow("sysusr.mast_unit", "dlr_asgn_no=?", DEALER_ASSIGN_NO.getValue());
        assertThat(row.get("unit_no").toString().trim()).isEqualTo(entry.getData().getUnit_no());
    }

    private void assertMastUnitFinUpdated(UpdateAssetInput input, UpdateAssetInput.Asset entry) {
        var row = testUtils.readRow("sysusr.mast_unit_fin", "dlr_asgn_no=?", DEALER_ASSIGN_NO.getValue());
        assertThat(row.get("loc_st_cd").toString().trim()).isEqualTo(entry.getData().getSt_loc_cd());
        assertThat(row.get("loc_city_cd").toString().trim()).isEqualTo(entry.getData().getCity_loc_cd());
    }

    private void assertMastUnitExtUpdated(UpdateAssetInput input, UpdateAssetInput.Asset entry) {
        var row = testUtils.readRow("sysusr.mast_unit_ext", "dlr_asgn_no=?", DEALER_ASSIGN_NO.getValue());
        assertThat(row.get("curr_odom").toString()).isEqualTo(entry.getData().getCurr_odom_reading().toString());
        assertThat(row.get("curr_odom_src").toString()).isEqualTo(entry.getData().getCurr_odom_src_cd().toString());
    }

    private UpdateAssetInput createUpdateAssetInput() {
        return UpdateAssetInput
                .builder()
                .value(
                        List.of(UpdateAssetInput.Asset.builder()
                                .key(
                                        AssetKey
                                                .builder()
                                                .spin_asset_id(SPIN_ASSET_ID)
                                                .build()
                                )
                                .data(
                                        AssetInput
                                                .builder()
                                                .bkdn("RETL-GLOB-T0454-0510")
                                                .body_style("LR RH SLIDE 148")
                                                .city_loc_cd("0400")
                                                .cli_asset_id("DECAL")
                                                .cnty_prov_loc_cd("109")
                                                .competitor_unit_no("1A")
                                                .contract_cd("LS")
                                                .contract_no("5778LX")
                                                .drvr_id("1906969")
                                                .engine_cd("G")
                                                .fac_ord_no("KE2YL241")
                                                .gvw(BigDecimal.valueOf(8600.0))
                                                .hvut_cd("*")
                                                .invy_stat_cd("4")
                                                .make("TSLA")
                                                .model("MODEL S")
                                                .model_cd("E2Y")
                                                .model_yr("19")
                                                .no_of_cyl(6)
                                                .no_of_doors("3")
                                                .out_of_stock_ind("N")
                                                .phh_asset_id("UNKNOWN")
                                                .prod_class_cd("LT")
                                                .spin_org_id(20267)
                                                .unit_no("07392")
                                                .vin("1FTYE2YM7")
                                                .series("CARGOX")
                                                .st_loc_cd("20")
                                                .spin_competitor_org_id(10)
                                                .prev_cli_no("ABC")
                                                .prev_unit_no("ABC")
                                                .prev_cli_asset_id("ABC")
                                                .curr_odom_reading(100)
                                                .curr_odom_src_cd("A")
                                                .curr_odom_dt(LocalDate.parse("2015-04-30"))
                                                .city_loc_cd("TOR")
                                                .driverInfo(AssetDriverInfo.builder()
                                                        .drvr_chng_dt(LocalDate.parse("2015-03-30"))
                                                        .drvrAddr(PostalAddressInfoInput.builder()
                                                                .addr_line1("123 Sesame Street")
                                                                .addr_line2("Unit 100")
                                                                .cnty_nm("Canada")
                                                                .postcode("A1A 1A1")
                                                                .st_prov_abbr_cd("ON")
                                                                .city_nm("Toronto")
                                                                .build())
                                                        .build())
                                                .build()
                                )
                                .build())).build();
    }

    private void insertDAN() {
        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.ast_id_mapping (spin_asset_id, corp_cd, dan, audit_insert_dt, audit_insert_login, audit_insert_pgm, ast_del_from_src_ind, edb_asset_id)
                        VALUES (%d, 'FA', '%s', CURRENT_DATE, 'eapi', 'eapi', 'N', 93176770);
                        """.formatted(SPIN_ASSET_ID.getValue(), DEALER_ASSIGN_NO.getValue()));
    }

    private void insertAssetData() {
        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.mast_unit (client_no,corp_cd,unit_no,cli_ast_no,repl_client_no,repl_unit_no,repl_cli_ast_no,cur_evt_seq_no,cur_evt,cur_evt_dt_c,cur_evt_dt_yr,cur_evt_dt_mo,cur_evt_dt_da,reqsn_no,cont_typ_cd,mo_reqd,emr_ind,ttl_cert_st,bkdn,ttl_own,ttl_co_nm,ttl_own_1st,ttl_own_mi,ttl_own_midrest,ttl_own_lst,ttl_addr1,ttl_addr2,ttl_city,ttl_cnty,ttl_st,ttl_zip,ttl_no,ttl_loc,ttl_iss_dt_c,ttl_iss_dt_yr,ttl_iss_dt_mo,ttl_iss_dt_da,tag_no,tag_iss_st,reg_xpir_dt_c,reg_xpir_dt_yr,reg_xpir_dt_mo,reg_xpir_dt_da,filler2,filler3,vin_decode_stat_cd,filler6,cli_drv_no,filler7,vndr_market_id,filler9,uvc,group_cd,drv_strt_dt_c,drv_strt_dt_yr,drv_strt_dt_mo,drv_strt_dt_da,dlr_asgn_no,fac_ord_no,vin_1st_9,vin_10_11,vin_lst_6,phh_ast_id,invy_sta_ind,prod_clas,prod_line,mod_yr,nmpl,series,model,mod_cd,cyl,pwr_seat_cd,pwr_str_cd,pwr_brk_cd,pwr_lck_cd,pwr_wndw_cd,tnt_gls_cd,crz_cntrl_cd,tlt_whl_cd,radio_cd,trans_typ_cd,trans_spds,eng_cd,doors,ac_cd,dfrst_cd,vt_cd,no_of_comp,mfg_sell_dlr,sell_dlr_eeo,sell_dlr_fee,mfg_dlv_dlr,dlv_dlr_eeo,dlv_dlr_fee,dlv_phh_dlr_no,sel_phh_dlr_no,dlv_dlr_dlan,dlv_typ,trk_key_no,ign_key_no,hvut_cd,hp,shp_wt,gvwr,whl_bas,brk_cd,tcv_audit_insert_date,tcv_audit_insert_userid,tcv_audit_insert_program,tcv_audit_update_date,tcv_audit_update_userid,tcv_audit_update_program) VALUES
                         ('%s','FA','07392','107392         ','CLI001','        ','               ',810,'ACTIVE    ',20,23,5,17,' ','  ','    ',' ','  ','0000005455-001-000                        ','                                             ','                                             ','               ',' ','              ','                    ','                              ','                              ','                    ','                       ','  ','         ','               ',' ',0,0,0,0,'CPJ6955             ','CN',20,24,12,31,' ','               ','1','              ','A14215233           ','                    ','  ','         ','          ','    ',0,0,0,0,'%s','            ','3GNAXWEG6','PS','211531','UNKNOWN     ','4','LT','CHEV','23','EQUINOX                  ','RS                  ','4D SUV AWD          ','1XY26     ',0,' ',' ',' ',' ',' ',' ',' ',' ',' ','A','  ',' ',' ',' ',' ',' ',0,'          ',' ',0.00,'          ',' ',0.00,'     ','     ','     ',' ','      ','      ',' ',0.0,0,2100,0,' ','2023-05-16 16:10:14.748','TC28230','XFC_WEB:Bulk-saveAsset','2023-12-08 04:13:53.23496',' ',' ');
                        """.formatted(PREVIOUS_CLIENT_NO.getValue(), DEALER_ASSIGN_NO.getValue())
        );
        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.mast_unit_fin
                        (dlr_asgn_no, corp_cd, client_no, bkdn, unit_no, cli_ast_no, pur_typ_cd, loc_st_cd, loc_city_cd, loc_cnty_cd, cont_no, cont_typ, lse_typ, cli_yr_end, acc_mgt_svc_ind, flt_adm_svc_ind, flt_svc_ind, reimb_svc_ind, veh_rpt_svc_ind, cpn_bk_ind, mrm_svc_ind, fuel_svc_ind, ec_svc_ind, ins_svc_ind, bkdn_chrg_ind, rerg_svc_ind, bas_inv_amt, bas_inv_amt_typ, hldb_amt, sl_tax_amt, cli_prc_amt, unit_prc_amt, vnd_inv_amt, rebat_amt, drv_pd_opt_amt, intrm_int_ind, tot_cap_cst, tot_aqstn_cst, lndr_no, lsor_no, fin_src, fs_int_rt_ind, src_of_fnd, rnt_tax_pay_cd, bs_cst, bs_typ, cur_bk_val, cur_odom, cur_odom_c, cur_odom_yr, cur_odom_mo, cur_odom_da, in_svc_c, in_svc_yr, in_svc_mo, in_svc_da, mo_in_svc, calc_pref_cd, cum_dif_ind, lse_term, lse_bill_cyc, lse_rt_typ, lse_int_rt, int_rt_max, pref_int_rt_ind, prc_sbsdy, bill_int_rt_ind, lndr_yr_cd, prc_id, prc_eff_c, prc_eff_yr, prc_eff_mo, prc_eff_da, var_prc_ind, lse_depr_ovrd, adj_depr_pct, init_rnt_c, init_rnt_yr, init_rnt_mo, init_rnt_da, lst_rnt_c, lst_rnt_yr, lst_rnt_mo, lst_rnt_da, init_bill_c, init_bill_yr, init_bill_mo, init_bill_da, lst_bill_c, lst_bill_yr, lst_bill_mo, lst_bill_da, era_svc_ind, itc_pct, incc_svc_ind, inpl_svc_ind, mgt_fee, mgt_fee_ind, addl_mgt_fee, addl_mgt_ind, mgt_fee_max, post_term_fee, post_term_ind, rsdl_bk_val, lst_mo_amrt, cur_mo_amrt, nxt_mo_amrt, ytd_amrt, cli_ytd_amrt, ltd_amrt, lst_mo_int, cur_mo_int, nxt_mo_int, ytd_int, cli_ytd_int, ltd_int, lst_mo_rnt_tax, cur_mo_rnt_tax, nxt_mo_rnt_tax, ytd_rnt_tax, cli_ytd_rnt_tax, ltd_rnt_tax, lst_mo_mgt_fee, cur_mo_mgt_fee, nxt_mo_mgt_fee, ytd_mgt_fee, cli_ytd_mgt_fee, ltd_mgt_fee, phh_split, sell_fee, tot_cown_bill, otr_amrt, cum_dif, ytd_int_var, cmpnd_int, otr_c, otr_yr, otr_mo, otr_da, sl_typ, sl_c, sl_yr, sl_mo, sl_da, sl_amt, sld_tax_amt, sld_tax_loc, fin_hst_cr_c, fin_hst_cr_yr, fin_hst_cr_mo, fin_hst_cr_da, bkdn_eff_c, bkdn_eff_yr, bkdn_eff_mo, bkdn_eff_da, cli_pd_opt_amt, ir_svc_ind, interim_rent_ind, rt_attr_c, rt_attr_yr, rt_attr_mo, rt_attr_da, pu_svc_ind, ext_lease_ind, unit_addl_int_amt, addl_int_effdt_cc, addl_int_effdt_yy, addl_int_effdt_mm, addl_int_effdt_dd, fund_ind, cap_freeze_ind, term_type, maturity_date_cc, maturity_date_yy, maturity_date_mm, maturity_date_dd, tcv_audit_insert_date, tcv_audit_insert_userid, tcv_audit_insert_program, tcv_audit_update_date, tcv_audit_update_userid, tcv_audit_update_program)
                        VALUES('%s', 'FA', '%s', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, '', 0, 0, 0, 0, 0, 0, 0, '', 0, 0, '', '', '', '', '', '', 0, '', 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, '', '', '', '', '', 0, 0, '', 0, '', '', '', 0, 0, 0, 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', 0, '', '', 0, '', 0, '', 0, 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', 0, 0, 0, 0, 0, 0, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, '', '', 0, 0, 0, 0, '', '', 0, '', '', '', '', '', '', '', '', '', '', '', GETDATE(), '', '', GETDATE(), '', '');
                        """.formatted(DEALER_ASSIGN_NO.getValue(), PREVIOUS_CLIENT_NO.getValue())
        );
        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.mast_unit_ext
                        (dlr_asgn_no, corp_cd, curr_odom, curr_odom_dt, curr_odom_src, curr_odom_uom_cd, asgn_stat_cd, asgn_stat_dt, reassign_dt, competitor_org_id, competitor_unit_no, notify_competitor_sold_ind, alt_unit_no, tag_typ_cd, title_issue_cd, phh_title_dt, title_loc_cd, title_out_rsn_cd, title_retention_cli_ind, cli_stat_title_ind, cli_stat_bill_of_sale_ind, cli_stat_tax_afd_ind, cli_title_mail_dt, lien_ind, po_no, prod_sub_class_cd, bus_mfg_cd, hazmat_carrier_ind, audit_insert_dt, audit_insert_tm, audit_insert_login, audit_insert_pgm, audit_update_dt, audit_update_tm, audit_update_login, audit_update_pgm, tcv_audit_insert_date, tcv_audit_insert_userid, tcv_audit_insert_program, tcv_audit_update_date, tcv_audit_update_userid, tcv_audit_update_program, cgm_ast_ind)
                        VALUES('%s', 'FA', 0, GETDATE(), '', '', '', GETDATE(), GETDATE(), 0, '', 0, '', 0, 0, GETDATE(), 0, 0, 0, 0, 0, 0, GETDATE(), 0, '', '', '', 0, GETDATE(), CURRENT_TIMESTAMP, '', '', GETDATE(), CURRENT_TIMESTAMP, '', '', GETDATE(), '', '', GETDATE(), '', '', 'N'::bpchar);
                        """.formatted(DEALER_ASSIGN_NO.getValue())
        );
        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.driver
                        (driv_nm_last, driv_nm_first, driv_nm_midinit, driv_nm_midrest, cli_driv_no, company_name, driv_addr1, driv_addr2, driv_city, driv_cnty, driv_state, driv_zipcode, driv_phone_area, driv_phone_no, driv_phone_extn, corp_cd, client_no, unit_no, soundex_last, soundex_first, tcv_audit_insert_date, tcv_audit_insert_userid, tcv_audit_insert_program, tcv_audit_update_date, tcv_audit_update_userid, tcv_audit_update_program)
                        VALUES('', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 'FA', '%s', '07392', '', '', GETDATE(), '', '', GETDATE(), '', '');
                        """.formatted(PREVIOUS_CLIENT_NO.getValue())
        );
        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.shared_inv
                        (client_no, unit_no, unit_curr_stat, unit_prev_stat, xref_curr_stat, xref_prev_stat, xref_client_no, xref_unit_no, purchase_type, mfg_order_no, initial_cost, book_value, lease_term, lev_lease_type, billing_yr, billing_mo, ec_lt_accid_exp, ec_lt_maint_exp, ec_odometer, lease_co_owned, initial_record, sub_no, div_no, branch_no, finance_source, vin, unit_type, car_mfg_code, car_make_code, car_model_yr, car_line_code, car_series_code, car_doors, car_body_style, car_cylinders, car_trans, car_radio, car_ps, car_pb, car_ac,car_tinted_all, car_tinted_ws, car_vinyl_int, car_vinyl_roof, sell_dealer_no, buy_dealer_no, buy_region, buy_state_code, buy_city_code, buy_cnty_code, delivery_yr, delivery_mo, delivery_da, bill_sale_code, init_rent_yr, init_rent_mo, last_rent_yr, last_rent_mo, sold_yr, sold_mo, sold_da, title_reg_name, driv_name, driv_address, driv_city, driv_state, driv_zipcode, driv_buy_region, driv_state_code, driv_city_code, driv_cnty_code, driv_assign_yr, driv_assign_mo, driv_assign_da, rental_tax_code, ttl_ln_flag, ttl_filler1, ttl_psn_cd, ttl_filler2, ttl_brngp_ind, ttl_brngp_typ, ttl_filler3, ttl_brngp_mo, ttl_brngp_week, ttl_filler4, title_state, title_iss_yr, title_iss_mo, title_iss_da, title_ret_cli, title_inhouse, title_received, title_corr, title_dup_req, title_dup_rcv, title_iss_buyer, title_iss_cli, title_iss_ret, title_iss_driv, title_iss_c_a, phh_title_yr, phh_title_mo, phh_title_da, cli_stat_title,cli_stat_b_of_s, cli_stat_tx_afd, cli_stat_reg, cli_title_yr, cli_title_mo, cli_title_da, lic_plate_no,lic_state, lic_iss_yr, lic_iss_mo, lic_iss_da, cycle_no, cycle_no_latest, pw_no, pw_no_latest, sale_reason, lease_rate, tax_paid_yr, tax_paid_state, tax_paid_city, tax_paid_cnty, filler, eq_filler1, eq_model_yr, eq_ident1, eq_ident2, eq_ident3, eq_cost_class, eq_filler2, trk_mfg_code, trk_make_code, trk_model_yr, trk_style, trk_wb, trk_weight, trk_engine, trk_displ,trk_trans, trk_axle, trk_ps, trk_pb, trk_trlr_make, trk_trlr_style, trk_spec_eq, trk_fhut_code, phh_inv_rcv_mo, phh_inv_rcv_yr, no_416_ind, tcv_audit_insert_dt, tcv_audit_insert_login, tcv_audit_insert_pgm, tcv_audit_update_dt, tcv_audit_update_login, tcv_audit_update_pgm)
                        VALUES ('%s', '07392', '', '', '', '', '', '', '', '', 0.00, 0.00, '', '', 0, 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, 0, '', 0, 0, 0, 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', 23, 12, 30, '', '', '', '', '', '', '', '', '', '', '', '', 12, 12, 12, '', '', '', '', '', '', '', '', '', '', '', 12, 12, 12, '', '', '', '', 12, 12, 12, '', '', 12, 12, 12, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '2023-12-08 04:13:53.23496', '', '', NULL, NULL, NULL);
                        """.formatted(PREVIOUS_CLIENT_NO.getValue())
        );

        testUtils.executeSQL(
                """
                        INSERT INTO sysusr.org_id_mapping
                        (corp_cd, cli_no, spin_org_id, edb_org_id, audit_insert_dt, audit_insert_login, audit_insert_pgm, cli_del_from_src_ind)
                        VALUES ('FA', 'CLI003', 20267, 467021, CURRENT_TIMESTAMP, '', '', 'N');
                        """
        );
    }
}
