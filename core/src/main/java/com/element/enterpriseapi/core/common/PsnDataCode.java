package com.element.enterpriseapi.core.common;

import lombok.Getter;

@Getter
public enum PsnDataCode {
    PRIMARY_POSTAL_ADDRESS(16);

    private final int code;

    PsnDataCode(int code) {
        this.code = code;
    }
}
