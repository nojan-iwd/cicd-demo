package com.element.enterpriseapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomMessageRouter implements MessageRoutingCallback {
    private final ObjectMapper mapper;

    @Override
    public String routingResult(Message<?> message) {
        log.info("Routing message");
        try {
            Map input = mapper.readValue(((byte[]) message.getPayload()), Map.class);
            String functionName = ((String) ((Map) input.get("info")).get("fieldName"));
            log.info("Routing to {}", functionName);
            return functionName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
