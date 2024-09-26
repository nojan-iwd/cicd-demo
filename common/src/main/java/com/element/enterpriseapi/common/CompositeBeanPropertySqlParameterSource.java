package com.element.enterpriseapi.common;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class CompositeBeanPropertySqlParameterSource implements SqlParameterSource {
    private final BeanPropertySqlParameterSource beanPropertySqlParameterSource;
    private final MapSqlParameterSource mapSqlParameterSource;

    public CompositeBeanPropertySqlParameterSource(Object object, Map<String, ?> additionalParams) {
        this.beanPropertySqlParameterSource = new BeanPropertySqlParameterSource(object);
        this.mapSqlParameterSource = new MapSqlParameterSource(additionalParams);
    }

    @Override
    public String[] getParameterNames() {
        String[] array1 = beanPropertySqlParameterSource.getParameterNames();
        String[] array2 = mapSqlParameterSource.getParameterNames();
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .distinct()
                .toArray(String[]::new);
    }

    @Override
    public String getTypeName(String paramName) {
        if (mapSqlParameterSource.hasValue(paramName)) {
            return mapSqlParameterSource.getTypeName(paramName);
        }
        return beanPropertySqlParameterSource.getTypeName(paramName);
    }

    @Override
    public int getSqlType(String paramName) {
        if (mapSqlParameterSource.hasValue(paramName)) {
            return mapSqlParameterSource.getSqlType(paramName);
        }
        return beanPropertySqlParameterSource.getSqlType(paramName);
    }

    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        if (mapSqlParameterSource.hasValue(paramName)) {
            return mapSqlParameterSource.getValue(paramName);
        }
        return beanPropertySqlParameterSource.getValue(paramName);
    }

    @Override
    public boolean hasValue(String paramName) {
        boolean beanHasValue = mapSqlParameterSource.hasValue(paramName);
        if (beanHasValue) {
            return true;
        }
        return beanPropertySqlParameterSource.hasValue(paramName);
    }
}
