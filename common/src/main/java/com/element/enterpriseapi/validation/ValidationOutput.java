package com.element.enterpriseapi.validation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationOutput {
    public static final ValidationOutput SUCCESS = ValidationOutput.builder().success(true).errorMessages(List.of()).build();

    private final boolean success;
    private final List<String> errorMessages;
}
