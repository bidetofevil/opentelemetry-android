/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.library.log.internal

import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.OpenTelemetryInstance
import io.embrace.opentelemetry.kotlin.getLogger
import io.embrace.opentelemetry.kotlin.noop
import io.opentelemetry.android.instrumentation.InstallationContext

@OptIn(ExperimentalApi::class)
object LogRecordBuilderCreator {
    @Volatile
    var logger = OpenTelemetryInstance.noop().getLogger("io.opentelemetry.android.log.noop")

    @JvmStatic
    fun configure(context: InstallationContext) {
        logger = context.openTelemetryKotlin.getLogger("io.opentelemetry.android.log")
    }

    @JvmStatic
    fun printStacktrace(throwable: Throwable): String = throwable.stackTraceToString()

    @JvmStatic
    fun getTypeName(throwable: Throwable): String = throwable.javaClass.canonicalName ?: throwable.javaClass.simpleName
}
