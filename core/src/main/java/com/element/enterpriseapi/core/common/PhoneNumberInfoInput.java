package com.element.enterpriseapi.core.common;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@SuppressWarnings("java:S116")
public class PhoneNumberInfoInput {
    String ext_pager_pin;
    String phone_no;
    String phone_no_mask;
    boolean pref_mthd_ind;
}
