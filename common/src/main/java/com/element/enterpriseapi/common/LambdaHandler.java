package com.element.enterpriseapi.common;

import com.element.enterpriseapi.exception.InputValidationException;

import java.util.function.Function;

public interface LambdaHandler<T, R> extends Function<T, R> {
    void validateInput(T input) throws InputValidationException;

    R executeInTransaction(T input);

    R createErrorResponse(T input, Exception exception);
}
