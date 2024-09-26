package com.element.enterpriseapi.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InputValidationException extends Exception {
    private final List<String> errorMessages;

    public InputValidationException() {
        super("Invalid input");
        this.errorMessages = List.of();
    }

    public InputValidationException(List<String> errorMessages) {
        super("Invalid input. Details: [" + String.join("; ", errorMessages) + "]");
        this.errorMessages = errorMessages;
    }
}
