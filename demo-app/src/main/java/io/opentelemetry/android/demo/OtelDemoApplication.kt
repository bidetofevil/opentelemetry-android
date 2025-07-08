/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.demo

import android.annotation.SuppressLint
import android.app.Application
import android.os.Trace
import android.os.Trace.beginSection
import android.os.Trace.endSection
import android.util.Log
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.k2j.OpenTelemetrySdk
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.OpenTelemetryRumBuilder
import io.opentelemetry.android.agent.OpenTelemetryRumInitializer
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.features.diskbuffering.DiskBufferingConfig
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.incubator.logs.ExtendedLogRecordBuilder
import io.opentelemetry.api.logs.LogRecordBuilder
import io.opentelemetry.api.metrics.LongCounter
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter

const val TAG = "otel.demo"

@ExperimentalApi
class OtelDemoApplication : Application() {
    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Initializing the opentelemetry-android-agent")
        // 10.0.2.2 is apparently a special binding to the host running the emulator
        try {
            val startTimestamp = System.nanoTime()
            val diskBufferingConfig = DiskBufferingConfig(
                enabled = true, maxCacheSize = 10_000_000, debugEnabled = true
            val config =
                OtelRumConfig()
                    .setGlobalAttributes(Attributes.of(stringKey("toolkit"), "jetpack compose"))
                    .setDiskBufferingConfig(diskBufferingConfig)
            beginSection("otel-rum-init")
            rum = OpenTelemetryRumBuilder.create(this, config).build()
            endSection()
//            rum = OpenTelemetryRumInitializer.initialize(
//                application = this,
//                endpointBaseUrl = "http://10.0.2.2:4318",
//                rumConfig = config
//            )
            beginSection("otel-log-init")
            kotlinTracer("android-agent")?.createSpan(
                name = "otel-android-init",
                startTimestamp = startTimestamp
            )?.end(System.nanoTime())

            Log.d(TAG, "RUM session started: " + rum!!.rumSessionId)
        } catch (e: Exception) {
            Log.e(TAG, "Oh no!", e)
        } finally {
            endSection()
        }

        // This is needed to get R8 missing rules warnings.
        initializeOtelWithGrpc()
    }

    // This is not used but it's needed to verify that our consumer proguard rules cover this use case.
    private fun initializeOtelWithGrpc() {
        val builder = OpenTelemetryRum.builder(this)
            .addSpanExporterCustomizer {
                OtlpGrpcSpanExporter.builder().build()
            }
            .addLogRecordExporterCustomizer {
                OtlpGrpcLogRecordExporter.builder().build()
            }

        // This is an overly-cautious measure to prevent R8 from discarding away the whole method
        // in case it identifies that it's actually not doing anything meaningful.
        if (System.currentTimeMillis() < 0) {
            print(builder)
        }
    }

    companion object {
        var rum: OpenTelemetryRum? = null

        @OptIn(ExperimentalApi::class)
        fun kotlinTracer(name: String): io.embrace.opentelemetry.kotlin.tracing.Tracer? =
            kotlinSdk?.tracerProvider?.getTracer(name)

        fun counter(name: String): LongCounter? {
            return rum?.openTelemetry?.meterProvider?.get("demo.app")?.counterBuilder(name)?.build()
        }

        fun eventBuilder(scopeName: String, eventName: String): LogRecordBuilder {
            val logger = rum?.openTelemetry?.logsBridge?.loggerBuilder(scopeName)?.build()
            var builder: ExtendedLogRecordBuilder = logger?.logRecordBuilder() as ExtendedLogRecordBuilder
            return builder.setEventName(eventName)
        }

        @OptIn(ExperimentalApi::class)
        val kotlinSdk: OpenTelemetrySdk? by lazy {
            rum?.openTelemetry?.let { OpenTelemetrySdk(it) }
        }
    }
}
