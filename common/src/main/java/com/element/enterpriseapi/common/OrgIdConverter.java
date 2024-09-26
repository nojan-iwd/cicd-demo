package com.element.enterpriseapi.common;

public interface OrgIdConverter {
    EdbOrgId convert(SpinOrgId spinOrgId);

    ClientNoXref resolve(SpinOrgId spinOrgId);
}
