package com.element.enterpriseapi.core.common;

import lombok.Getter;

@Getter
public enum AddressType {
    BUSINESS(37),
    MAILING(38),
    PRIMARY(39);

    private final int code;

    AddressType(int code) {
        this.code = code;
    }
}
