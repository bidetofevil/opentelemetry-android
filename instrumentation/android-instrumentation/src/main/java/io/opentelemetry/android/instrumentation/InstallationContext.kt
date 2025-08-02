/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation

import android.app.Application
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.OpenTelemetry
import io.embrace.opentelemetry.kotlin.OpenTelemetryInstance
import io.embrace.opentelemetry.kotlin.compatWithOtelJava
import io.opentelemetry.android.session.SessionProvider

@OptIn(ExperimentalApi::class)
data class InstallationContext(
    val application: Application,
    val openTelemetry: io.opentelemetry.api.OpenTelemetry,
    val sessionProvider: SessionProvider,
    val openTelemetryKotlin: OpenTelemetry = OpenTelemetryInstance.compatWithOtelJava(openTelemetry),
)

@OptIn(ExperimentalApi::class)
fun createInstallationContext(
    application: Application,
    openTelemetry: io.opentelemetry.api.OpenTelemetry,
    sessionProvider: SessionProvider,
): InstallationContext = InstallationContext(application = application, openTelemetry = openTelemetry, sessionProvider = sessionProvider)
