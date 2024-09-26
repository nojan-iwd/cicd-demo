package com.element.enterpriseapi.validation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationField {
    private String errorMessage;
    private String name;
    private Integer status;
}
