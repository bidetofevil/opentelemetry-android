/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.library.log

import android.util.Log
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import io.embrace.opentelemetry.kotlin.logging.model.SeverityNumber
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.instrumentation.library.log.internal.LogRecordBuilderCreator
import io.opentelemetry.instrumentation.library.log.internal.LogRecordBuilderCreator.getTypeName
import io.opentelemetry.instrumentation.library.log.internal.LogRecordBuilderCreator.printStacktrace
import io.opentelemetry.semconv.ExceptionAttributes

@OptIn(ExperimentalApi::class)
object AndroidLogSubstitutions {
    val tagKey: AttributeKey<String> = AttributeKey.stringKey("android.log.tag")

    @JvmStatic
    fun substitutionForVerbose(
        tag: String?,
        message: String,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.TRACE,
        )

        return Log.v(tag, message)
    }

    @JvmStatic
    fun substitutionForVerbose2(
        tag: String?,
        message: String,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.TRACE,
            throwable = throwable,
        )

        return Log.v(tag, message, throwable)
    }

    @JvmStatic
    fun substitutionForDebug(
        tag: String?,
        message: String,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.DEBUG,
        )

        return Log.d(tag, message)
    }

    @JvmStatic
    fun substitutionForDebug2(
        tag: String?,
        message: String,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.DEBUG,
            throwable = throwable,
        )

        return Log.d(tag, message, throwable)
    }

    @JvmStatic
    fun substitutionForInfo(
        tag: String?,
        message: String,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.INFO,
        )

        return Log.i(tag, message)
    }

    @JvmStatic
    fun substitutionForInfo2(
        tag: String?,
        message: String,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.INFO,
            throwable = throwable,
        )

        return Log.i(tag, message, throwable)
    }

    @JvmStatic
    fun substitutionForWarn(
        tag: String?,
        message: String,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.WARN,
        )
        return Log.w(tag, message)
    }

    @JvmStatic
    fun substitutionForWarn2(
        tag: String?,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            severityNumber = SeverityNumber.WARN,
            throwable = throwable,
        )

        return Log.w(tag, throwable)
    }

    @JvmStatic
    fun substitutionForWarn3(
        tag: String?,
        message: String,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.WARN,
            throwable = throwable,
        )

        return Log.w(tag, message, throwable)
    }

    @JvmStatic
    fun substitutionForError(
        tag: String?,
        message: String,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.ERROR,
        )
        return Log.e(tag, message)
    }

    @JvmStatic
    fun substitutionForError2(
        tag: String?,
        message: String,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.ERROR,
            throwable = throwable,
        )

        return Log.e(tag, message, throwable)
    }

    @JvmStatic
    fun substitutionForWtf(
        tag: String?,
        message: String,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.UNKNOWN,
        )
        return Log.wtf(tag, message)
    }

    @JvmStatic
    fun substitutionForWtf2(
        tag: String?,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            severityNumber = SeverityNumber.UNKNOWN,
            throwable = throwable,
        )

        return Log.wtf(tag, throwable)
    }

    @JvmStatic
    fun substitutionForWtf3(
        tag: String?,
        message: String,
        throwable: Throwable,
    ): Int {
        log(
            tag = tag,
            message = message,
            severityNumber = SeverityNumber.UNKNOWN,
            throwable = throwable,
        )

        return Log.wtf(tag, message, throwable)
    }

    private fun log(
        tag: String?,
        message: String? = null,
        severityNumber: SeverityNumber,
        throwable: Throwable? = null,
    ) {
        LogRecordBuilderCreator.logger.log(
            severityNumber = severityNumber,
            body = message,
        ) {
            setStringAttribute(tagKey.key, tag.orEmpty())
            if (throwable != null) {
                setStringAttribute(ExceptionAttributes.EXCEPTION_TYPE.key, getTypeName(throwable))
                setStringAttribute(ExceptionAttributes.EXCEPTION_STACKTRACE.key, printStacktrace(throwable))
            }
        }
    }
}
