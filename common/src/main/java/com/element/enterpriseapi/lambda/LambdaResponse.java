package com.element.enterpriseapi.lambda;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class LambdaResponse {
    private Boolean success;
    private Integer rowCount;
    private String message;
}
