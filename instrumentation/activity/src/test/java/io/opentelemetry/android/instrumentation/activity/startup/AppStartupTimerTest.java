/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation.activity.startup;

import static io.opentelemetry.android.common.RumConstants.START_TYPE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.embrace.opentelemetry.kotlin.tracing.Span;
import io.embrace.opentelemetry.kotlin.tracing.Tracer;
import io.opentelemetry.android.test.common.FakeOpenTelemetry;
import io.opentelemetry.sdk.trace.data.SpanData;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppStartupTimerTest {
    final FakeOpenTelemetry otelTesting = new FakeOpenTelemetry();
    private Tracer tracer;

    @BeforeEach
    void setup() {
        tracer = otelTesting.getTracer("testTracer");
    }

    @Test
    void start_end() {
        AppStartupTimer appStartupTimer = new AppStartupTimer();
        Span startSpan = appStartupTimer.start(tracer);
        assertNotNull(startSpan);
        appStartupTimer.end();

        List<SpanData> spans = otelTesting.getSpans();
        assertEquals(1, spans.size());
        SpanData spanData = spans.get(0);

        assertEquals("AppStart", spanData.getName());
        assertEquals("cold", spanData.getAttributes().get(START_TYPE_KEY));
    }

    @Test
    void multi_end() {
        AppStartupTimer appStartupTimer = new AppStartupTimer();
        appStartupTimer.start(tracer);
        appStartupTimer.end();
        appStartupTimer.end();

        assertEquals(1, otelTesting.getSpans().size());
    }

    @Test
    void multi_start() {
        AppStartupTimer appStartupTimer = new AppStartupTimer();
        appStartupTimer.start(tracer);
        assertSame(appStartupTimer.start(tracer), appStartupTimer.start(tracer));

        appStartupTimer.end();
        assertEquals(1, otelTesting.getSpans().size());
    }
}
