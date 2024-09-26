package com.element.enterpriseapi.core.person;

import com.element.enterpriseapi.common.CustomMessageConverter;
import com.element.enterpriseapi.common.RawLambdaPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdatePersonInputMessageConverter extends CustomMessageConverter<UpdatePersonInput> {

    protected UpdatePersonInputMessageConverter(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected TypeReference<UpdatePersonInput> getInputType() {
        return new TypeReference<>() {
        };
    }

    @Override
    protected UpdatePersonInput convert(RawLambdaPayload rawLambdaPayload) {
        UpdatePersonInput result = new UpdatePersonInput();
        result.setValue(mapper.convertValue(rawLambdaPayload.getBody(), new TypeReference<>() {
        }));
        return result;
    }

}
