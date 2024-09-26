package com.element.enterpriseapi.core.common;

import lombok.Getter;

@Getter
public enum CommType {
    PRIMARY_BUSINESS_PHONE(40),
    SECONDARY_BUSINESS_PHONE(41),
    EMAIL(42),
    FAX(43),
    PRIMARY_HOME_PHONE(44),
    SECONDARY_HOME_PHONE(45),
    CELL_PHONE(46),
    VOICE_MAIL(47),
    PAGER(48),
    ALTERNATE_EMAIL(49),
    HOME_FAX(50),
    SECONDARY_BUSINESS_FAX(51),
    URL(52);

    private final int code;

    CommType(int code) {
        this.code = code;
    }
}
