package com.element.enterpriseapi;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class EnterpriseAPIHandler<T, S extends EnterpriseAPIService<T>> {
    private T event;
    private S service;

    public boolean validateInput() {
        return service.validateInput(event);
    }

    public boolean validateBusiness() {
        return service.validateBusiness(event);
    }

    public void execute() {
        service.execute(event);
    }
}
