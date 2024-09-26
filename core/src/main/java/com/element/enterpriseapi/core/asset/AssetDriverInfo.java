package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.common.DbFieldGroup;
import com.element.enterpriseapi.core.common.PostalAddressInfoInput;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class AssetDriverInfo {
    @DbFieldGroup(name = "driver", column = "driv_nm_first")
    String drvr_first_nm;
    @DbFieldGroup(name = "driver", column = "driv_nm_midinit")
    String drvr_nm_midinit;
    @DbFieldGroup(name = "driver", column = "driv_nm_midrest")
    String drvr_nm_midrest;
    @DbFieldGroup(name = "driver", column = "driv_nm_last")
    String drvr_last_nm;
    @DbFieldGroup(name = "driver", column = "driv_phone_area")
    String drvr_phone_area_cd;
    @DbFieldGroup(name = "driver", column = "driv_phone_no")
    String drvr_local_phone_no;
    @DbFieldGroup(name = "driver", column = "driv_phone_extn")
    String drvr_phone_ext;
    @DbFieldGroup(name = "driver", column = "client_no")
    String client_no_drvr;
    @DbFieldGroup(name = "shared_inv", column = "driv_assign_yr")
    Integer drvr_chng_dt_year;
    @DbFieldGroup(name = "shared_inv", column = "driv_assign_mo")
    Integer drvr_chng_dt_month;
    @DbFieldGroup(name = "shared_inv", column = "driv_assign_da")
    Integer drvr_chng_dt_day;
    @DbFieldGroup(name = "driver", column = "company_name")
    String drvr_co_nm;
    String drvr_mid_nm;
    LocalDate drvr_chng_dt;
    PostalAddressInfoInput drvrAddr;

    public String getDrvr_nm_midinit() {
        return StringUtils.isNotBlank(drvr_mid_nm) ? drvr_mid_nm.substring(0, 1) : null;
    }

    public String getDrvr_nm_midrest() {
        return StringUtils.isNotBlank(drvr_mid_nm) ? drvr_mid_nm.substring(1) : null;
    }

    public Integer getDrvr_chng_dt_year() {
        return drvr_chng_dt != null ? drvr_chng_dt.getYear() % 100 : null;
    }

    public Integer getDrvr_chng_dt_month() {
        return drvr_chng_dt != null ? drvr_chng_dt.getMonthValue() : null;
    }

    public Integer getDrvr_chng_dt_day() {
        return drvr_chng_dt != null ? drvr_chng_dt.getDayOfMonth() : null;
    }
}
