package com.element.enterpriseapi.core.common;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@SuppressWarnings("java:S116")
public class EmailAddressInfoInput {
    String email_addr;
    boolean pref_mthd_ind;
}
