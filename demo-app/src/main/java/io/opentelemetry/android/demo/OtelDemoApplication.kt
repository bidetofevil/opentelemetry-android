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
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.features.diskbuffering.DiskBufferingConfiguration
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.incubator.events.EventBuilder
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.logs.internal.SdkEventLoggerProvider
import io.opentelemetry.sdk.resources.Resource
import java.util.UUID

const val TAG = "otel.demo"

class OtelDemoApplication : Application() {
    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Initializing the opentelemetry-android-agent")

        try {
            val customRumBuilder = FakeOpenTelemetryRumBuilder.getDefault(this)
            customRumBuilder.mergeResource(
                Resource.builder().put("custom_id", UUID.randomUUID().toString()).build()
            )

            rum = AndroidAgent.createOpenTelemetryRum(
                application = this,
                rumBuilder = customRumBuilder
            )
            Log.d(TAG, "RUM session started: " + rum!!.rumSessionId)
        } catch (e: Exception) {
            Log.e(TAG, "Oh no!", e)
        }
    }

    object AndroidAgent {
        fun createOpenTelemetryRum(
            application: Application,
            appName: String? = null,
            appVersion: String? = null,
            endpointConfig: EndpointConfig = EndpointConfig(),
            rumBuilder: OpenTelemetryRumBuilder = FakeOpenTelemetryRumBuilder.getDefault(application)
        ): OpenTelemetryRum {
            appName?.apply {
                rumBuilder.mergeResource(
                    Resource.builder().put("service.name", this).build()
                )
            }

            appVersion?.apply {
                rumBuilder.mergeResource(
                    Resource.builder().put("service.version", this).build()
                )
            }

            endpointConfig.apply {
                spanIngestUrl?.apply {
                    rumBuilder.addSpanExporterCustomizer {
                        OtlpHttpSpanExporter.builder()
                            .setEndpoint(this)
                            .build()
                    }
                }
                logIngestUrl?.apply {
                    rumBuilder.addLogRecordExporterCustomizer {
                        OtlpHttpLogRecordExporter.builder()
                            .setEndpoint(this)
                            .build()
                    }
                }
            }

            return rumBuilder.build()
        }
    }

    object FakeOpenTelemetryRumBuilder {
        fun getDefault(application: Application): OpenTelemetryRumBuilder {
            val diskBufferingConfig =
                DiskBufferingConfiguration.builder()
                    .setEnabled(true)
                    .setMaxCacheSize(10_000_000)
                    .build()
            val config =
                OtelRumConfig()
                    .setGlobalAttributes(Attributes.of(stringKey("toolkit"), "jetpack compose"))
                    .setDiskBufferingConfiguration(diskBufferingConfig)

            return OpenTelemetryRum.builder(application, config)
        }
    }

    data class EndpointConfig(
        val spanIngestUrl: String? = "http://10.0.2.2:4318/v1/traces",
        val logIngestUrl: String? = "http://10.0.2.2:4318/v1/logs"
    )

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
