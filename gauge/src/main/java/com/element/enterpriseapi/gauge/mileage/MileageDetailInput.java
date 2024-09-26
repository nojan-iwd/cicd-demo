package com.element.enterpriseapi.gauge.mileage;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@Jacksonized
@AllArgsConstructor
@Builder
public class MileageDetailInput {

    @Size(max = 4)
    String exp_typ_cd;
    @Size(max = 4)
    String uom_typ_cd;
    @Digits(integer = 12, fraction = 2)
    BigDecimal exp_amt;
    Integer exp_qnty;
}
