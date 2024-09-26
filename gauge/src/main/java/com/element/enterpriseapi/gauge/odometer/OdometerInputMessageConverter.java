package com.element.enterpriseapi.gauge.odometer;

import com.element.enterpriseapi.common.CustomMessageConverter;
import com.element.enterpriseapi.common.RawLambdaPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OdometerInputMessageConverter extends CustomMessageConverter<OdometerInput> {

    public OdometerInputMessageConverter(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected TypeReference<OdometerInput> getInputType() {
        return new TypeReference<>() {
        };
    }

    @Override
    protected OdometerInput convert(RawLambdaPayload rawLambdaPayload) {
        OdometerInput result = new OdometerInput();
        result.setValue(mapper.convertValue(rawLambdaPayload.getBody(), OdometerInput.Odometer.class));
        return result;
    }
}
