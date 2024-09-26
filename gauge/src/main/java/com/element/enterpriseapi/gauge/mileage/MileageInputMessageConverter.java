package com.element.enterpriseapi.gauge.mileage;

import com.element.enterpriseapi.common.CustomMessageConverter;
import com.element.enterpriseapi.common.RawLambdaPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MileageInputMessageConverter extends CustomMessageConverter<MileageInput> {

    private static final String SPIN_MILEAGE_RPT_ID = "spinMileageRptId";

    public MileageInputMessageConverter(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected TypeReference<MileageInput> getInputType() {
        return new TypeReference<>() {
        };
    }

    @Override
    protected MileageInput convert(RawLambdaPayload rawLambdaPayload) {
        MileageInput result = new MileageInput();
        result.setValue(mapper.convertValue(rawLambdaPayload.getBody(), MileageInput.Mileage.class));
        if (rawLambdaPayload.getParams().get(SPIN_MILEAGE_RPT_ID) instanceof Integer id) {
            result.getValue().setSpin_mileage_rpt_id(id);
        }
        return result;
    }
}
