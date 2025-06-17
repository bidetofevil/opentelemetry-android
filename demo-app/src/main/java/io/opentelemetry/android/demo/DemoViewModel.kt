/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.embrace.opentelemetry.kotlin.ExperimentalApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalApi::class)
class DemoViewModel : ViewModel() {
    val sessionIdState = MutableStateFlow("? unknown ?")
    private val tracer = OtelDemoApplication.kotlinTracer("otel.demo")

    init {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                // TODO: Do some work here maybe
            }
        }
    }

    private fun updateSession() {
        // TODO
    }

    private fun sendTrace(
        type: String,
        value: Float,
    ) {
        // A metric should be a better fit, but for now we're using spans.
        tracer?.run {
            createSpan(
                name = type,
                spanKind = io.embrace.opentelemetry.kotlin.tracing.SpanKind.INTERNAL
            ) {
                setDoubleAttribute("value", value.toDouble())
            }.end()
        }
    }
}
