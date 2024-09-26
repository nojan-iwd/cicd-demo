package com.element.enterpriseapi.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public final class DanXref {
    DealerAssignNo dan;
    String corp_cd;
    String unmodified_unit_no;
    String unmodified_client_no;

    public Country country() {
        return switch (corp_cd) {
            case "FA" -> Country.US;
            case "CA" -> Country.CA;
            case "MX" -> Country.MX;
            default ->
                    throw new IllegalArgumentException("Empty corp_cd found for dealer_assign_no %s".formatted(dan.getValue()));
        };
    }
}
