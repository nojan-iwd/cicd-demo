package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.core.common.EmailAddressInfoInput;
import com.element.enterpriseapi.core.common.PhoneNumberInfoInput;
import com.element.enterpriseapi.core.common.PostalAddressInfoInput;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class PersonInput {
    PostalAddressInfoInput primaryAddress;
    EmailAddressInfoInput alternateEmailAddress;
    PhoneNumberInfoInput cellularPhone;
}
