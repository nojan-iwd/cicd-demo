package com.element.enterpriseapi.common;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public abstract class StrongType<T> {
    private final T value;

    protected StrongType(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @JsonValue
    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrongType<?> that = (StrongType<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
