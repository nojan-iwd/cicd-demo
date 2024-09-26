package com.element.enterpriseapi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.util.List;

public interface WithJson {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @SneakyThrows
    default <T> T fromJson(String json, Class<T> type) {
        return OBJECT_MAPPER.readValue(json, type);
    }

    @SneakyThrows
    default <T> List<T> listFromJson(String json, Class<T> type) {
        var typeReference = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, type);
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    @SneakyThrows
    default String toJson(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

}
