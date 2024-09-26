package com.element.enterpriseapi.exception;

public class EDBAssetIdNotFoundException extends RuntimeException{

    public EDBAssetIdNotFoundException(Long spinAssetId) {
        super(String.format("EDB Asset Id for SPIN Asset Id %d not found", spinAssetId));
    }
}
