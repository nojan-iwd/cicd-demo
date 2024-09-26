package com.element.enterpriseapi.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class RawLambdaPayload {
    private static final String NAME_INPUT = "input";
    private static final String NAME_REQUEST = "request";
    private static final String NAME_ARGUMENTS = "arguments";
    private static final String NAME_HEADERS = "headers";

    private final Map<String, String> headers;

    @Getter
    private Map<String, Object> params;

    @Getter
    private final Object body;

    @JsonCreator
    public RawLambdaPayload(@JsonProperty(NAME_REQUEST) Map<String, Object> request,
                            @JsonProperty(NAME_ARGUMENTS) Map<String, Object> arguments) {
        headers = (Map<String, String>) request.get(NAME_HEADERS);
        params = arguments;
        body = params.get(NAME_INPUT);
    }

    public Optional<String> findHeader(String name) {
        return Optional.ofNullable(headers)
                .flatMap(hdr -> Optional.ofNullable(hdr.get(name)));
    }

    public boolean hasBody() {
        return body != null;
    }

}
