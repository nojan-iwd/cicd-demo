package com.element.enterpriseapi.core.common;

import com.element.enterpriseapi.common.DbFieldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("java:S116")
public class PostalAddressInfoInput {
    @DbFieldGroup(name = "driver", column = "driv_addr1")
    String addr_line1;
    @DbFieldGroup(name = "driver", column = "driv_addr2")
    String addr_line2;
    String addr_line3;
    String addr_line4;
    @DbFieldGroup(name = "driver", column = "driv_city")
    String city_nm;
    @DbFieldGroup(name = "driver", column = "driv_cnty")
    String cnty_nm;
    String iso_cntry_cd;
    @DbFieldGroup(name = "driver", column = "driv_zipcode")
    String postcode;
    @DbFieldGroup(name = "driver", column = "driv_state")
    String st_prov_abbr_cd;
    boolean pref_addr_ind;
}
