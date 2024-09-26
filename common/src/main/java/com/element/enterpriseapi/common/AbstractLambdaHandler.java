package com.element.enterpriseapi.common;

import com.element.enterpriseapi.lambda.LambdaInput;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractLambdaHandler<T extends LambdaInput, R> implements LambdaHandler<T, R>, BeanNameAware {

    private final PlatformTransactionManager txManager;
    private final OpenTelemetry otlp;
    private String beanName;

    @Override
    public R apply(T input) {
        Tracer tracer = otlp.getTracer("enterprise-api");
        recordMetrics(input.getAuditProgram(), input.getAuditLogin());
        Span span;
        if (!Strings.isBlank(input.getOtlpTraceParent())) {
            log.debug(String.format("Using traceparent header: %s", input.getOtlpTraceParent()));
            SpanContext spanContext = OtlpHeaderParser.parseTraceparentHeader(input.getOtlpTraceParent());
            span = tracer.spanBuilder(beanName)
                    .setParent(Context.current().with(Span.wrap(spanContext)))
                    .startSpan();
        } else {
            span = tracer.spanBuilder(beanName).startSpan();
        }
        TransactionStatus txStatus = beginTransaction();
        try (Scope scope = span.makeCurrent()) {
            validateInput(input);
            R result = executeInTransaction(input);
            txManager.commit(txStatus);
            return result;
        } catch (Exception e) {
            log.error("Exception occurred on lambda handler", e);
            span.recordException(e);
            txManager.rollback(txStatus);
            return createErrorResponse(input, e);
        } finally {
            span.end();
            prepareShutdown();
        }
    }

    private void prepareShutdown() {
        if (otlp instanceof OpenTelemetrySdk openTelemetrySdk) {
            log.info("Exporting OTLP metrics and traces");
            SdkMeterProvider meterProvider = openTelemetrySdk.getSdkMeterProvider();
            meterProvider.forceFlush().join(1500, MILLISECONDS);
            SdkTracerProvider tracerProvider = openTelemetrySdk.getSdkTracerProvider();
            tracerProvider.forceFlush().join(1500, MILLISECONDS);
        }
    }

    private TransactionStatus beginTransaction() {
        var txDef = new DefaultTransactionDefinition();
        txDef.setName(getClass().getSimpleName());
        txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return txManager.getTransaction(txDef);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    private void recordMetrics(String auditProgram, String auditLogin) {
        if (auditProgram != null) {
            Meter programMeter = otlp.getMeter("enterprise-api-program");
            LongCounter counter = programMeter.counterBuilder("audit-program")
                    .setDescription("Enterprise API Calling Program Name")
                    .setUnit("count")
                    .build();
            counter.add(1L, Attributes.of(AttributeKey.stringKey("audit.program"), auditProgram), Context.current());
        }
        // TODO Do we really need to record EAPI call "per user" ??? Not sure how useful is metric is
        if (auditLogin != null) {
            Meter loginMeter = otlp.getMeter("enterprise-api-login");
            LongCounter counter = loginMeter.counterBuilder("audit-login")
                    .setDescription("Enterprise API Calling User Name")
                    .setUnit("count")
                    .build();
            counter.add(1L, Attributes.of(AttributeKey.stringKey("audit.login"), auditLogin), Context.current());
        }
    }
}
