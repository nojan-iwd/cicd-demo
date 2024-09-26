package com.element.enterpriseapi.common;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Builder
@Value
public class DbFieldGroupResult {
    String setStatement;
    String insertColumns;
    String insertValues;
    List<String> columns;
    Map<String, Object> values;
    @Accessors(fluent = true)
    Boolean hasDataChanges;
}
