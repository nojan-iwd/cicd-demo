package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.common.SpinPersonId;
import com.element.enterpriseapi.core.common.EmailAddressInfoInput;
import com.element.enterpriseapi.core.common.PhoneNumberInfoInput;
import com.element.enterpriseapi.core.common.PostalAddressInfoInput;
import com.element.enterpriseapi.lambda.LambdaInput;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdatePersonInput extends LambdaInput<List<UpdatePersonInput.Person>> {

    @Value
    @Builder
    @Jacksonized
    public static class Person {
        PersonKey key;
        PersonInput data;

        public SpinPersonId getSpin_psn_id() {
            return key == null ? null : key.getSpin_psn_id();
        }

        public PostalAddressInfoInput getPrimaryAddress() {
            return data == null ? null : data.getPrimaryAddress();
        }

        public EmailAddressInfoInput getAlternateEmailAddress() {
            return data == null ? null : data.getAlternateEmailAddress();
        }

        public PhoneNumberInfoInput getCellularPhone() {
            return data == null ? null : data.getCellularPhone();
        }
    }
}

