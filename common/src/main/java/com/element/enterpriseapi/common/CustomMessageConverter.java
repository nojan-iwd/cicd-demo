package com.element.enterpriseapi.common;

import com.element.enterpriseapi.lambda.LambdaInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

@Slf4j
public abstract class CustomMessageConverter<T extends LambdaInput<?>> extends AbstractMessageConverter {
    private static final String HEADER_CLIENT_IDENTIFIER = "x-client-identifier";
    private static final String HEADER_LOGIN = "x-audit-login";
    private static final String HEADER_TRACE_PARENT = "traceparent";


    protected final ObjectMapper mapper;

    protected CustomMessageConverter(ObjectMapper mapper) {
        super(new MimeType("application", "LambdaInput"));
        this.mapper = mapper;
    }

    protected abstract TypeReference<T> getInputType();

    @Override
    protected boolean supports(Class<?> clazz) {
        boolean result = getInputType().getType().equals(clazz);
        log.info("Checking if {} can convert {}: {}", this.getClass().getName(), clazz.getName(), result);
        return result;
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, @Nullable Object conversionHint) {
        // TODO: Remove this log?  PII concerns.
        log.info(String.format("Starting conversion for message %s", message));
        byte[] messagePayload = getPayload(message);
        try {
            RawLambdaPayload rawLambdaPayload = mapper.readValue(messagePayload, RawLambdaPayload.class);
            if (rawLambdaPayload.hasBody()) {
                T result = convert(rawLambdaPayload);
                rawLambdaPayload.findHeader(HEADER_CLIENT_IDENTIFIER).ifPresent(result::setAuditProgram);
                rawLambdaPayload.findHeader(HEADER_LOGIN).ifPresent(result::setAuditLogin);
                rawLambdaPayload.findHeader(HEADER_TRACE_PARENT).ifPresent(result::setOtlpTraceParent);
                log.info("Converted input to {}", result);
                return result;
            } else {
                logger.warn("Input is malformed, returning");
                return message.getPayload();
            }
        } catch (Exception e) {
            log.warn("Error while converting", e);
            return message.getPayload();
        }
    }

    protected T convert(RawLambdaPayload rawLambdaPayload) {
        return mapper.convertValue(rawLambdaPayload.getBody(), getInputType());
    }

    private static byte[] getPayload(Message<?> message) {
        return switch (message.getPayload()) {
            case String str -> str.getBytes();
            case byte[] barr -> barr;
            default ->
                    throw new IllegalArgumentException("Unexpected payload type: " + message.getPayload().getClass());
        };
    }
}

