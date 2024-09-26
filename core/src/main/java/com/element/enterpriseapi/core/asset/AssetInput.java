package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.common.DbFieldGroup;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class AssetInput {
    Integer spin_org_id;
    @DbFieldGroup(name = "mast_unit")
    String unit_no;
    @DbFieldGroup(name = "mast_unit")
    String bkdn;
    @DbFieldGroup(name = "mast_unit")
    String series;
    @DbFieldGroup(name = "mast_unit")
    String hvut_cd;
    @DbFieldGroup(name = "mast_unit")
    String fac_ord_no;
    @DbFieldGroup(name = "mast_unit")
    Integer vndr_market_id;
    @DbFieldGroup(name = "mast_unit")
    String vin_decode_stat_cd;
    @DbFieldGroup(name = "mast_unit", column = "uvc")
    String vndr_trim_cd;
    @DbFieldGroup(name = "mast_unit", column = "group_cd")
    String vndr_group_cd;
    @DbFieldGroup(name = "mast_unit", column = "invy_sta_ind")
    String invy_stat_cd;
    @DbFieldGroup(name = "mast_unit", column = "cli_ast_no")
    String cli_asset_id;
    @DbFieldGroup(name = "mast_unit", column = "cli_drv_no")
    String drvr_id;
    @DbFieldGroup(name = "mast_unit", column = "vin_1st_9")
    String vin;
    @DbFieldGroup(name = "mast_unit", column = "prod_clas")
    String prod_class_cd;
    @DbFieldGroup(name = "mast_unit", column = "mod_yr")
    String model_yr;
    @DbFieldGroup(name = "mast_unit", column = "prod_line")
    String make;
    @DbFieldGroup(name = "mast_unit", column = "nmpl")
    String model;
    @DbFieldGroup(name = "mast_unit", column = "model")
    String body_style;
    @DbFieldGroup(name = "mast_unit", column = "mod_cd")
    String model_cd;
    @DbFieldGroup(name = "mast_unit", column = "cyl")
    Integer no_of_cyl;
    @DbFieldGroup(name = "mast_unit", column = "doors")
    String no_of_doors;
    @DbFieldGroup(name = "mast_unit", column = "eng_cd")
    String engine_cd;
    @DbFieldGroup(name = "mast_unit", column = "gvwr")
    BigDecimal gvw;
    @DbFieldGroup(name = "mast_unit", column = "phh_ast_id")
    String phh_asset_id;
    @DbFieldGroup(name = "mast_unit", column = "emr_ind")
    String out_of_stock_ind;
    @DbFieldGroup(name = "mast_unit", column = "repl_client_no")
    String prev_cli_no;
    @DbFieldGroup(name = "mast_unit", column = "repl_unit_no")
    String prev_unit_no;
    @DbFieldGroup(name = "mast_unit", column = "repl_cli_ast_no")
    String prev_cli_asset_id;
    @DbFieldGroup(name = "mast_unit", column = "whl_bas")
    BigDecimal wheelbase;
    @DbFieldGroup(name = "mast_unit", column = "client_no")
    String client_no_mu;
    @DbFieldGroup(name = "mast_unit_fin")
    LocalDateTime bkdn_eff_dt;
    @DbFieldGroup(name = "mast_unit_fin", column = "cont_no")
    String contract_no;
    @DbFieldGroup(name = "mast_unit_fin", column = "cont_typ")
    String contract_cd;
    @DbFieldGroup(name = "mast_unit_fin", column = "client_no")
    String client_no_muf;
    @DbFieldGroup(name = "mast_unit_fin", column = "loc_city_cd")
    String city_loc_cd;
    @DbFieldGroup(name = "mast_unit_fin", column = "loc_cnty_cd")
    String cnty_prov_loc_cd;
    @DbFieldGroup(name = "mast_unit_fin", column = "loc_st_cd")
    String st_loc_cd;
    @DbFieldGroup(name = "mast_unit_ext")
    String asgn_stat_cd;
    @DbFieldGroup(name = "mast_unit_ext")
    LocalDateTime asgn_stat_dt;
    @DbFieldGroup(name = "mast_unit_ext")
    LocalDateTime reassign_dt;
    @DbFieldGroup(name = "mast_unit_ext")
    String competitor_unit_no;
    @DbFieldGroup(name = "mast_unit_ext", column = "competitor_org_id")
    Integer spin_competitor_org_id;
    @DbFieldGroup(name = "mast_unit_ext", column = "curr_odom")
    Integer curr_odom_reading;
    @DbFieldGroup(name = "mast_unit_ext", column = "curr_odom_dt")
    LocalDate curr_odom_dt;
    @DbFieldGroup(name = "mast_unit_ext", column = "curr_odom_src")
    String curr_odom_src_cd;
    @DbFieldGroup(name = "st_pd_rnt_tax", column = "trw")
    BigDecimal taxable_registered_weight_amt;
    AssetDriverInfo driverInfo;
}
