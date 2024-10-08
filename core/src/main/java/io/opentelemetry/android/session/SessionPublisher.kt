/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.session

interface SessionPublisher {
    fun addObserver(observer: SessionObserver)
}
