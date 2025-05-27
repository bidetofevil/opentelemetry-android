/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation

import android.app.Application
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.k2j.OpenTelemetrySdk
import io.opentelemetry.android.session.SessionProvider
import io.opentelemetry.api.OpenTelemetry

data class InstallationContext(
    val application: Application,
    val openTelemetry: OpenTelemetry,
    val sessionProvider: SessionProvider,
) {
    @OptIn(ExperimentalApi::class)
    val kotlinOtel: io.embrace.opentelemetry.kotlin.OpenTelemetry = OpenTelemetrySdk(openTelemetry)
}
