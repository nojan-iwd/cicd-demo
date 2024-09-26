package com.element.enterpriseapi.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class LambdaInput<T> {
    T value;
    String auditProgram;
    String auditLogin;
    String otlpTraceParent;
}
