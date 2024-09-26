package com.element.enterpriseapi.common;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class OtlpHeaderParser {

    public static SpanContext parseTraceparentHeader(String traceParentHeader) {
        // Parse traceparent header according to W3C Trace Context specification
        // Example traceparent: "00-traceId-spanId-flags"
        try {
            String[] parts = traceParentHeader.split("-");
            if (parts.length < 4) {
                return SpanContext.getInvalid();
            }
            String traceId = parts[1];
            String spanId = parts[2];
            TraceFlags traceFlags = "01".equals(parts[3]) ? TraceFlags.getSampled() : TraceFlags.getDefault();

            return SpanContext.createFromRemoteParent(traceId, spanId, traceFlags, TraceState.getDefault());
        } catch (Exception ex) {
            log.error("Error while parsing traceparent header", ex);
            return SpanContext.getInvalid();
        }
    }
}
