/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation

import android.app.Application
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.OpenTelemetry
import io.embrace.opentelemetry.kotlin.OpenTelemetryInstance
import io.embrace.opentelemetry.kotlin.context.Context
import io.embrace.opentelemetry.kotlin.createOpenTelemetryKotlin
import io.opentelemetry.android.session.SessionProvider

@OptIn(ExperimentalApi::class)
data class InstallationContext(
    val application: Application,
    val openTelemetry: io.opentelemetry.api.OpenTelemetry,
    val sessionProvider: SessionProvider,
    val openTelemetryKotlin: OpenTelemetry = OpenTelemetryInstance.createOpenTelemetryKotlin(),
    val rootContext: Context = openTelemetryKotlin.objectCreator.context.root(),
) {
    override fun hashCode(): Int {
        var result = application.hashCode()
        result = 31 * result + openTelemetry.hashCode()
        result = 31 * result + sessionProvider.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallationContext

        if (application != other.application) return false
        if (openTelemetry != other.openTelemetry) return false
        if (sessionProvider != other.sessionProvider) return false

        return true
    }
}

@OptIn(ExperimentalApi::class)
fun createInstallationContext(
    application: Application,
    openTelemetry: io.opentelemetry.api.OpenTelemetry,
    sessionProvider: SessionProvider,
): InstallationContext = InstallationContext(application = application, openTelemetry = openTelemetry, sessionProvider = sessionProvider)
