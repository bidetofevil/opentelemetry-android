/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation.activity

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.OpenTelemetryInstance
import io.embrace.opentelemetry.kotlin.decorateKotlinApi
import io.embrace.opentelemetry.kotlin.getTracer
import io.embrace.opentelemetry.kotlin.testing.junit5.OpenTelemetryExtension
import io.embrace.opentelemetry.kotlin.tracing.Tracer
import io.mockk.every
import io.mockk.mockk
import io.opentelemetry.android.instrumentation.createInstallationContext
import io.opentelemetry.android.internal.services.Services
import io.opentelemetry.android.internal.services.visiblescreen.VisibleScreenTracker
import io.opentelemetry.android.session.SessionProvider
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalApi::class)
@RunWith(AndroidJUnit4::class)
class ActivityLifecycleInstrumentationTest {
    @RegisterExtension
    val otelTesting: OpenTelemetryExtension = OpenTelemetryExtension()
    private lateinit var activityLifecycleInstrumentation: ActivityLifecycleInstrumentation
    private lateinit var application: Application
    private lateinit var sessionProvider: SessionProvider
    private lateinit var services: Services

    private lateinit var tracer: Tracer

    @Before
    fun setUp() {
        application = RuntimeEnvironment.getApplication()
        tracer = otelTesting.openTelemetry.getTracer("io.opentelemetry.lifecycle")
        activityLifecycleInstrumentation = ActivityLifecycleInstrumentation()
        services = mockk()
        sessionProvider = mockk()
        every { services.visibleScreenTracker }.returns(mockk<VisibleScreenTracker>())
    }

    @Test
    fun `Installing instrumentation starts AppStartupTimer`() {
        val ctx =
            createInstallationContext(
                application,
                OpenTelemetryInstance.decorateKotlinApi(otelTesting.openTelemetry),
                sessionProvider,
            )
        activityLifecycleInstrumentation.install(ctx)
    }
}
