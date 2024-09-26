package com.element.enterpriseapi.common;

public abstract class StrongIntegerType extends StrongType<Integer> {
    protected StrongIntegerType(Integer value) {
        super(value);
    }

    protected StrongIntegerType(String value) {
        super(Integer.valueOf(value));
    }
}
