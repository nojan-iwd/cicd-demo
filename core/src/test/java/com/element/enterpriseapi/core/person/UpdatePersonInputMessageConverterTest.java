package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.WithJson;
import com.element.enterpriseapi.WithResources;
import com.element.enterpriseapi.common.SpinPersonId;
import com.element.enterpriseapi.core.common.EmailAddressInfoInput;
import com.element.enterpriseapi.core.common.PhoneNumberInfoInput;
import com.element.enterpriseapi.core.common.PostalAddressInfoInput;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdatePersonInputMessageConverterTest implements WithJson, WithResources {

    private static final String JSON = """
            {
                "info": {
                    "fieldName": "UpdatePerson"
                },
                "arguments": {
                    "input": [
                        {
                            "key": {
                                "spin_psn_id": 98765
                            },
                            "data": {
                                "primaryAddress": {
                                    "addr_line1": "line 1",
                                    "addr_line2": "line 2",
                                    "addr_line3": null,
                                    "addr_line4": null,
                                    "city_nm": "Burlington",
                                    "cnty_nm": "Halton",
                                    "iso_cntry_cd": "CA",
                                    "postcode": "L7L5F5",
                                    "st_prov_abbr_cd": "ON"
                                },
                                "alternateEmailAddress": {
                                    "email_addr": "joe@email.com",
                                    "pref_mthd_ind": true
                                },
                                "cellularPhone": {
                                    "ext_pager_pin": "1010",
                                    "phone_no": "4164164164",
                                    "phone_no_mask": "***",
                                    "pref_mthd_ind": true
                                }
                            }
                        }
                    ]
                },
                "request": {
                    "headers": {
                        "x-client-identifier": "x2bsl",
                        "x-audit-login": "test-user"
                    }
                }
            }
            """;

    private final UpdatePersonInputMessageConverter converter = new UpdatePersonInputMessageConverter(OBJECT_MAPPER);

    @Test
    void deserialize() {
        UpdatePersonInput actual = (UpdatePersonInput) converter.fromMessage(new GenericMessage<>(JSON), UpdatePersonInput.class);
        assertThat(actual).isEqualTo(
                UpdatePersonInput
                        .builder()
                        .auditLogin("test-user")
                        .auditProgram("x2bsl")
                        .value(List.of(
                                UpdatePersonInput.Person.builder()
                                        .key(
                                                PersonKey
                                                        .builder()
                                                        .spin_psn_id(new SpinPersonId(98765))
                                                        .build()
                                        )
                                        .data(
                                                PersonInput
                                                        .builder()
                                                        .primaryAddress(
                                                                PostalAddressInfoInput
                                                                        .builder()
                                                                        .addr_line1("line 1")
                                                                        .addr_line2("line 2")
                                                                        .city_nm("Burlington")
                                                                        .st_prov_abbr_cd("ON")
                                                                        .cnty_nm("Halton")
                                                                        .postcode("L7L5F5")
                                                                        .iso_cntry_cd("CA")
                                                                        .build()
                                                        )
                                                        .alternateEmailAddress(
                                                                EmailAddressInfoInput
                                                                        .builder()
                                                                        .email_addr("joe@email.com")
                                                                        .pref_mthd_ind(true)
                                                                        .build()
                                                        )
                                                        .cellularPhone(
                                                                PhoneNumberInfoInput
                                                                        .builder()
                                                                        .ext_pager_pin("1010")
                                                                        .phone_no("4164164164")
                                                                        .phone_no_mask("***")
                                                                        .pref_mthd_ind(true)
                                                                        .build()
                                                        )
                                                        .build())
                                        .build()
                        ))
                        .build()
        );
    }

}
