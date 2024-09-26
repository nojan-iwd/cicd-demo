package com.element.enterpriseapi.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClientNoXref {
    ClientNo cli_no;
    String corp_cd;
}
