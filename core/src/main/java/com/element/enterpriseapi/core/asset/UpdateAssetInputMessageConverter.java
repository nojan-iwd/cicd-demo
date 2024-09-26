package com.element.enterpriseapi.core.asset;

import com.element.enterpriseapi.common.CustomMessageConverter;
import com.element.enterpriseapi.common.RawLambdaPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateAssetInputMessageConverter extends CustomMessageConverter<UpdateAssetInput> {

    protected UpdateAssetInputMessageConverter(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected TypeReference<UpdateAssetInput> getInputType() {
        return new TypeReference<>() {
        };
    }

    @Override
    protected UpdateAssetInput convert(RawLambdaPayload rawLambdaPayload) {
        UpdateAssetInput result = new UpdateAssetInput();
        result.setValue(mapper.convertValue(rawLambdaPayload.getBody(), new TypeReference<>() {
        }));
        return result;
    }

}
