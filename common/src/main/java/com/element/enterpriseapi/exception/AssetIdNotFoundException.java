package com.element.enterpriseapi.exception;

public class AssetIdNotFoundException extends RuntimeException{

    public AssetIdNotFoundException(Long assetId) {
        super(String.format("Asset Id %d not found", assetId));
    }
}
