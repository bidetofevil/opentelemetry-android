/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.android.instrumentation.fragment;

import static io.opentelemetry.android.instrumentation.common.ActiveSpan.INVALID_ACTIVE_SPAN;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.embrace.opentelemetry.kotlin.ExperimentalApi;
import io.embrace.opentelemetry.kotlin.tracing.Tracer;
import io.embrace.opentelemetry.kotlin.tracing.model.Span;
import io.embrace.opentelemetry.kotlin.tracing.model.SpanKind;
import io.opentelemetry.android.common.RumConstants;
import io.opentelemetry.android.instrumentation.common.ActiveSpan;
import io.opentelemetry.api.common.AttributeKey;

@ExperimentalApi
class FragmentTracer {
    static final AttributeKey<String> FRAGMENT_NAME_KEY = AttributeKey.stringKey("fragment.name");

    private final String fragmentName;
    private final String screenName;
    private final Tracer tracer;
    private final ActiveSpan activeSpan;

    private FragmentTracer(Builder builder, Tracer tracer) {
        this.tracer = tracer;
        this.fragmentName = builder.getFragmentName();
        this.screenName = builder.screenName;
        this.activeSpan = builder.activeSpan;
    }

    FragmentTracer startSpanIfNoneInProgress(String action) {
        if (activeSpan.spanInProgress()) {
            return this;
        }
        activeSpan.startSpan(() -> createSpan(action));
        return this;
    }

    FragmentTracer startFragmentCreation() {
        activeSpan.startSpan(() -> createSpan("Created"));
        return this;
    }

    private Span createSpan(String spanName) {
        final Span span =
                tracer.createSpan(
                        spanName,
                        null,
                        SpanKind.INTERNAL,
                        null,
                        attributeContainer -> {
                            attributeContainer.setStringAttribute(
                                    FRAGMENT_NAME_KEY.getKey(), fragmentName);
                            return null;
                        });
        // do this after the span is started, so we can override the default screen.name set by the
        // RumAttributeAppender.
        span.setStringAttribute(RumConstants.SCREEN_NAME_KEY.getKey(), screenName);
        return span;
    }

    void endActiveSpan() {
        activeSpan.endActiveSpan();
    }

    FragmentTracer addPreviousScreenAttribute() {
        activeSpan.addPreviousScreenAttribute(fragmentName);
        return this;
    }

    FragmentTracer addEvent(String eventName) {
        activeSpan.addEvent(eventName);
        return this;
    }

    static Builder builder(Fragment fragment) {
        return new Builder(fragment);
    }

    static class Builder {
        private final Fragment fragment;
        public String screenName = "";
        @Nullable private Tracer tracer = null;
        private ActiveSpan activeSpan = INVALID_ACTIVE_SPAN;

        public Builder(Fragment fragment) {
            this.fragment = fragment;
        }

        Builder setTracer(Tracer tracer) {
            this.tracer = tracer;
            return this;
        }

        public Builder setScreenName(String screenName) {
            this.screenName = screenName;
            return this;
        }

        Builder setActiveSpan(ActiveSpan activeSpan) {
            this.activeSpan = activeSpan;
            return this;
        }

        public String getFragmentName() {
            return fragment.getClass().getSimpleName();
        }

        FragmentTracer build() {
            if (activeSpan == INVALID_ACTIVE_SPAN) {
                throw new IllegalStateException("activeSpan must be configured.");
            }
            if (tracer == null) {
                throw new IllegalStateException("tracer must be configured.");
            }
            return new FragmentTracer(this, tracer);
        }
    }
}
