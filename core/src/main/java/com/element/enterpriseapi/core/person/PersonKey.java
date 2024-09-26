package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.common.SpinPersonId;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@SuppressWarnings("java:S116")
public class PersonKey {
    SpinPersonId spin_psn_id;
}
