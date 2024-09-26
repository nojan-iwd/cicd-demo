package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.common.SpinPersonId;
import com.element.enterpriseapi.lambda.LambdaResponse;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("java:S116")
public class UpdatePersonResponse extends LambdaResponse {
    SpinPersonId spin_psn_id;
}
