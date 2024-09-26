package com.element.enterpriseapi.exception;

public class EnterpriseApiException extends RuntimeException {
    public EnterpriseApiException(String message) {
        super(message);
    }

    public EnterpriseApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnterpriseApiException(Throwable cause) {
        super(cause);
    }
}
