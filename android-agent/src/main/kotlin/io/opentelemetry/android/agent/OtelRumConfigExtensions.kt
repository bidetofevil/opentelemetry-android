/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.agent

import android.app.Application
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.OpenTelemetryRumBuilder
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.features.diskbuffering.DiskBufferingConfiguration
import io.opentelemetry.android.instrumentation.AndroidInstrumentationLoader
import io.opentelemetry.android.instrumentation.activity.ActivityLifecycleInstrumentation
import io.opentelemetry.android.instrumentation.anr.AnrInstrumentation
import io.opentelemetry.android.instrumentation.common.ScreenNameExtractor
import io.opentelemetry.android.instrumentation.crash.CrashDetails
import io.opentelemetry.android.instrumentation.crash.CrashReporterInstrumentation
import io.opentelemetry.android.instrumentation.fragment.FragmentLifecycleInstrumentation
import io.opentelemetry.android.instrumentation.network.NetworkChangeInstrumentation
import io.opentelemetry.android.instrumentation.slowrendering.SlowRenderingInstrumentation
import io.opentelemetry.android.internal.services.network.data.CurrentNetwork
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor
import java.time.Duration

/**
 * Convenience functions to allow configuring the default instrumentations through the [OtelRumConfig] object, for example:
 *
 * ```
 * OtelRumConfig()
 *  .setSessionTimeout(Duration.ofSeconds(10)) // Real OtelRumConfig function
 *  .setSlowRenderingDetectionPollInterval(Duration.ofSeconds(5)) // Extension function
 *  .disableScreenAttributes() // Real OtelRumConfig function
 * ```
 */

fun OtelRumConfig.setActivityTracerCustomizer(customizer: (Tracer) -> Tracer): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(ActivityLifecycleInstrumentation::class.java)
        ?.setTracerCustomizer(customizer)
    return this
}

fun OtelRumConfig.setActivityNameExtractor(screenNameExtractor: ScreenNameExtractor): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(ActivityLifecycleInstrumentation::class.java)
        ?.setScreenNameExtractor(screenNameExtractor)
    return this
}

fun OtelRumConfig.setFragmentTracerCustomizer(customizer: (Tracer) -> Tracer): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(FragmentLifecycleInstrumentation::class.java)
        ?.setTracerCustomizer(customizer)
    return this
}

fun OtelRumConfig.setFragmentNameExtractor(screenNameExtractor: ScreenNameExtractor): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(FragmentLifecycleInstrumentation::class.java)
        ?.setScreenNameExtractor(screenNameExtractor)
    return this
}

fun OtelRumConfig.addAnrAttributesExtractor(extractor: AttributesExtractor<Array<StackTraceElement>, Void>): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(AnrInstrumentation::class.java)
        ?.addAttributesExtractor(extractor)
    return this
}

fun OtelRumConfig.addCrashAttributesExtractor(extractor: AttributesExtractor<CrashDetails, Void>): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(CrashReporterInstrumentation::class.java)
        ?.addAttributesExtractor(extractor)
    return this
}

fun OtelRumConfig.addNetworkChangeAttributesExtractor(extractor: AttributesExtractor<CurrentNetwork, Void>): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(NetworkChangeInstrumentation::class.java)
        ?.addAttributesExtractor(extractor)
    return this
}

fun OtelRumConfig.setSlowRenderingDetectionPollInterval(interval: Duration): OtelRumConfig {
    AndroidInstrumentationLoader.getInstrumentation(SlowRenderingInstrumentation::class.java)
        ?.setSlowRenderingDetectionPollInterval(interval)
    return this
}

object Defaults {
    fun getDefaultRumBuilder(
        application: Application,
        config: OtelRumConfig = getDefaultConfig()
    ): OpenTelemetryRumBuilder {
        val builder = OpenTelemetryRumBuilder.create(application, config)

        // Put additional agent-applied defaults to the builder here

        return builder
    }

    fun getDefaultConfig(): OtelRumConfig {
        val diskBufferingConfig =
            DiskBufferingConfiguration.builder()
                .setEnabled(true)
                .setMaxCacheSize(10000000)
                .build()

        // Put additional agent-applied defaults in the config here

        return OtelRumConfig().setDiskBufferingConfiguration(diskBufferingConfig)
    }
}

fun createOpenTelemetryRum(
    application: Application,
    appName: String? = null,
    appVersion: String? = null,
    rumBuilder: OpenTelemetryRumBuilder = Defaults.getDefaultRumBuilder(application, Defaults.getDefaultConfig())
): OpenTelemetryRum {
    val resourceBuilder = OpenTelemetryRumBuilder.getDefaultResource()
    appName?.apply {
        resourceBuilder.put(AttributeKey.stringKey("service.name"), this)
    }

    appVersion?.apply {
        resourceBuilder.put(AttributeKey.stringKey("service.version"), this)
    }

    return rumBuilder.setResource(resourceBuilder.build()).build()
}