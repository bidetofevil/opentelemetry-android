/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:OptIn(ExperimentalApi::class)

package io.opentelemetry.android.test.common

import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.OpenTelemetry
import io.embrace.opentelemetry.kotlin.StatusCode
import io.embrace.opentelemetry.kotlin.attributes.AttributeContainer
import io.embrace.opentelemetry.kotlin.context.Context
import io.embrace.opentelemetry.kotlin.logging.Logger
import io.embrace.opentelemetry.kotlin.logging.LoggerProvider
import io.embrace.opentelemetry.kotlin.logging.SeverityNumber
import io.embrace.opentelemetry.kotlin.tracing.Link
import io.embrace.opentelemetry.kotlin.tracing.Span
import io.embrace.opentelemetry.kotlin.tracing.SpanContext
import io.embrace.opentelemetry.kotlin.tracing.SpanEvent
import io.embrace.opentelemetry.kotlin.tracing.SpanKind
import io.embrace.opentelemetry.kotlin.tracing.SpanRelationships
import io.embrace.opentelemetry.kotlin.tracing.TraceFlags
import io.embrace.opentelemetry.kotlin.tracing.TraceState
import io.embrace.opentelemetry.kotlin.tracing.Tracer
import io.embrace.opentelemetry.kotlin.tracing.TracerProvider
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.data.EventData
import io.opentelemetry.sdk.trace.data.LinkData
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.sdk.trace.data.StatusData

class FakeOpenTelemetry : OpenTelemetry {
    val spans = mutableListOf<SpanData>()

    override val loggerProvider: LoggerProvider = FakeLoggerProvider()
    override val tracerProvider: TracerProvider = FakeTracerProvider(spans)

    fun getTracer(name: String): Tracer = FakeTracer(spans)

    fun clearSpans() = spans.clear()
}

private class FakeLoggerProvider : LoggerProvider {
    override fun getLogger(
        name: String,
        version: String?,
        schemaUrl: String?,
        attributes: AttributeContainer.() -> Unit,
    ): Logger = FakeLogger()
}

private class FakeTracerProvider(
    private val createdSpans: MutableList<SpanData>,
) : TracerProvider {
    override fun getTracer(
        name: String,
        version: String?,
        schemaUrl: String?,
        attributes: AttributeContainer.() -> Unit,
    ): Tracer = FakeTracer(createdSpans)
}

private class FakeLogger : Logger {
    override fun log(
        body: String?,
        timestampNs: Long?,
        observedTimestampNs: Long?,
        context: Context?,
        severityNumber: SeverityNumber?,
        severityText: String?,
        attributes: AttributeContainer.() -> Unit,
    ) {
    }
}

private class FakeTracer(
    private val createdSpans: MutableList<SpanData>,
) : Tracer {
    override fun createSpan(
        name: String,
        parent: SpanContext?,
        spanKind: SpanKind,
        startTimestamp: Long?,
        action: SpanRelationships.() -> Unit,
    ): Span =
        FakeSpan(
            name = name,
            parent = parent,
        ).also {
            createdSpans.add(FakeSpanData(name = name))
        }
}

private class FakeSpan(
    override var name: String,
    override val parent: SpanContext?,
) : Span {
    override val spanContext: SpanContext = FakeSpanContext()
    override var status: StatusCode = StatusCode.Unset

    override fun end() {
    }

    override fun end(timestamp: Long) {
    }

    override fun addLink(
        spanContext: SpanContext,
        action: AttributeContainer.() -> Unit,
    ) {
    }

    override fun addEvent(
        name: String,
        timestamp: Long?,
        action: AttributeContainer.() -> Unit,
    ) {
    }

    override fun isRecording(): Boolean = false

    override fun events(): List<SpanEvent> = emptyList()

    override fun links(): List<Link> = emptyList()

    override fun setBooleanAttribute(
        key: String,
        value: Boolean,
    ) {
    }

    override fun setStringAttribute(
        key: String,
        value: String,
    ) {
    }

    override fun setLongAttribute(
        key: String,
        value: Long,
    ) {
    }

    override fun setDoubleAttribute(
        key: String,
        value: Double,
    ) {
    }

    override fun setBooleanListAttribute(
        key: String,
        value: List<Boolean>,
    ) {
    }

    override fun setStringListAttribute(
        key: String,
        value: List<String>,
    ) {
    }

    override fun setLongListAttribute(
        key: String,
        value: List<Long>,
    ) {
    }

    override fun setDoubleListAttribute(
        key: String,
        value: List<Double>,
    ) {
    }

    override fun attributes(): Map<String, Any> = emptyMap()
}

private class FakeSpanContext(
    override val isRemote: Boolean = false,
    override val isValid: Boolean = false,
    override val spanId: String = "",
    override val traceFlags: TraceFlags = FakeTraceFlags(isRandom = false, isSampled = false),
    override val traceId: String = "",
    override val traceState: TraceState = FakeTraceState(),
) : SpanContext

private class FakeTraceState : TraceState {
    override fun asMap(): Map<String, String> = emptyMap()

    override fun get(key: String): String? = null
}

private class FakeTraceFlags(
    override val isRandom: Boolean,
    override val isSampled: Boolean,
) : TraceFlags

private class FakeSpanData(
    private var name: String,
) : SpanData {
    override fun getName(): String = name

    override fun getKind(): io.opentelemetry.api.trace.SpanKind {
        TODO("Not yet implemented")
    }

    override fun getSpanContext(): io.opentelemetry.api.trace.SpanContext {
        TODO("Not yet implemented")
    }

    override fun getParentSpanContext(): io.opentelemetry.api.trace.SpanContext {
        TODO("Not yet implemented")
    }

    override fun getStatus(): StatusData {
        TODO("Not yet implemented")
    }

    override fun getStartEpochNanos(): Long {
        TODO("Not yet implemented")
    }

    override fun getAttributes(): Attributes {
        TODO("Not yet implemented")
    }

    override fun getEvents(): MutableList<EventData> {
        TODO("Not yet implemented")
    }

    override fun getLinks(): MutableList<LinkData> {
        TODO("Not yet implemented")
    }

    override fun getEndEpochNanos(): Long {
        TODO("Not yet implemented")
    }

    override fun hasEnded(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getTotalRecordedEvents(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalRecordedLinks(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalAttributeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getInstrumentationLibraryInfo(): InstrumentationLibraryInfo {
        TODO("Not yet implemented")
    }

    override fun getResource(): Resource {
        TODO("Not yet implemented")
    }
}
