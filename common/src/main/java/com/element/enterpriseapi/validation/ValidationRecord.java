package com.element.enterpriseapi.validation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationRecord {
    private Integer recordId;
    private Integer status;
    private List<ValidationField> fields;
}
