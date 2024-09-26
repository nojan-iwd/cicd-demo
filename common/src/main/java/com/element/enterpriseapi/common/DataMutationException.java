package com.element.enterpriseapi.common;

import com.element.enterpriseapi.exception.EnterpriseApiException;

public class DataMutationException extends EnterpriseApiException {
    public DataMutationException(String message) {
        super(message);
    }

    public DataMutationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataMutationException(Throwable cause) {
        super(cause);
    }
}
