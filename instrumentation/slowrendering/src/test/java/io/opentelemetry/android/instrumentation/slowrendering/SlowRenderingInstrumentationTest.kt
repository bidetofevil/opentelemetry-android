/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation.slowrendering

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.OpenTelemetry
import io.embrace.opentelemetry.kotlin.OpenTelemetryInstance
import io.embrace.opentelemetry.kotlin.decorateKotlinApi
import io.embrace.opentelemetry.kotlin.testing.junit5.OpenTelemetryExtension
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.opentelemetry.android.instrumentation.InstallationContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.Duration

@OptIn(ExperimentalApi::class)
@RunWith(AndroidJUnit4::class)
class SlowRenderingInstrumentationTest {
    @RegisterExtension
    val otelTesting: OpenTelemetryExtension = OpenTelemetryExtension()
    private lateinit var slowRenderingInstrumentation: SlowRenderingInstrumentation
    private lateinit var application: Application
    private lateinit var openTelemetry: OpenTelemetry

    @Before
    fun setUp() {
        application = mockk()
        openTelemetry = otelTesting.openTelemetry
        slowRenderingInstrumentation = SlowRenderingInstrumentation()
    }

    @Test
    fun `Verify default poll interval value`() {
        assertThat(slowRenderingInstrumentation.slowRenderingDetectionPollInterval).isEqualTo(
            Duration.ofSeconds(1),
        )
    }

    @Test
    fun `Changing poll interval value`() {
        slowRenderingInstrumentation.setSlowRenderingDetectionPollInterval(Duration.ofSeconds(2))

        assertThat(slowRenderingInstrumentation.slowRenderingDetectionPollInterval).isEqualTo(
            Duration.ofSeconds(2),
        )
    }

    @Test
    fun `Not changing poll interval value when provided value is negative`() {
        slowRenderingInstrumentation.setSlowRenderingDetectionPollInterval(Duration.ofSeconds(-2))

        assertThat(slowRenderingInstrumentation.slowRenderingDetectionPollInterval).isEqualTo(
            Duration.ofSeconds(1),
        )
    }

    @Config(sdk = [23])
    @Test
    fun `Not installing instrumentation on devices with API level lower than 24`() {
        val ctx = InstallationContext(application, OpenTelemetryInstance.decorateKotlinApi(openTelemetry), mockk())
        slowRenderingInstrumentation.install(ctx)

        verify {
            application wasNot Called
        }
    }

    @Config(sdk = [24, 25])
    @Test
    fun `Installing instrumentation on devices with API level equal or higher than 24`() {
        val capturedListener = slot<SlowRenderListener>()
        every { application.registerActivityLifecycleCallbacks(any()) } just Runs
        val ctx = InstallationContext(application, OpenTelemetryInstance.decorateKotlinApi(openTelemetry), mockk())
        slowRenderingInstrumentation.install(ctx)

        verify { application.registerActivityLifecycleCallbacks(capture(capturedListener)) }
    }
}
