/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.demo

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.OpenTelemetryRumBuilder
import io.opentelemetry.android.agent.Defaults
import io.opentelemetry.android.agent.createOpenTelemetryRum
import io.opentelemetry.android.agent.setSlowRenderingDetectionPollInterval
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.features.diskbuffering.DiskBufferingConfiguration
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.incubator.events.EventBuilder
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.logs.internal.SdkEventLoggerProvider
import java.time.Duration
import kotlin.math.log

const val TAG = "otel.demo"

class OtelDemoApplication : Application() {
    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Initializing the opentelemetry-android-agent")
        val config = Defaults.getDefaultConfig().apply {
            setGlobalAttributes(Attributes.of(stringKey("toolkit"), "jetpack compose"))
        }

        // 10.0.2.2 is apparently a special binding to the host running the emulator
        val spansIngestUrl = "http://10.0.2.2:4318/v1/traces"
        val logsIngestUrl = "http://10.0.2.2:4318/v1/logs"
        val otelRumBuilder = Defaults.getDefaultRumBuilder(this, config).apply {
            addSpanExporterCustomizer {
                OtlpHttpSpanExporter.builder()
                    .setEndpoint(spansIngestUrl)
                    .build()
            }

            addLogRecordExporterCustomizer {
                OtlpHttpLogRecordExporter.builder()
                    .setEndpoint(logsIngestUrl)
                    .build()
            }
        }

        try {
            rum = createOpenTelemetryRum(
                application = this,
                appName = "demo app",
                appVersion = "1.0.0",
                rumBuilder = otelRumBuilder
            )
            Log.d(TAG, "RUM session started: " + rum!!.rumSessionId)
        } catch (e: Exception) {
            Log.e(TAG, "Oh no!", e)
        }
    }

    companion object {
        var rum: OpenTelemetryRum? = null

        fun tracer(name: String): Tracer? {
            return rum?.openTelemetry?.tracerProvider?.get(name)
        }

        fun eventBuilder(scopeName: String, eventName: String): EventBuilder {
            val loggerProvider = rum?.openTelemetry?.logsBridge
            val eventLogger =
                SdkEventLoggerProvider.create(loggerProvider).get(scopeName)
            return eventLogger.builder(eventName)
        }
    }
}
