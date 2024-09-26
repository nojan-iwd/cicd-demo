package com.element.enterpriseapi;

public interface EnterpriseAPIService<T> {
    boolean validateInput(T event);

    boolean validateBusiness(T event);

    void execute(T event);
}