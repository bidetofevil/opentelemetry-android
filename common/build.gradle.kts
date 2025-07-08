plugins {
    id("otel.android-library-conventions")
    id("otel.publish-conventions")
}

description = "OpenTelemetry android common utils"

android {
    namespace = "io.opentelemetry.android.common"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    api(platform(libs.opentelemetry.platform.alpha))
    api(libs.opentelemetry.api)
    implementation(libs.opentelemetry.sdk)
    implementation(libs.opentelemetry.instrumentation.api)
    implementation(libs.opentelemetry.semconv.incubating)
    implementation(libs.androidx.core)
    api(libs.compat.kotlin.to.official)
    api(libs.opentelemetry.kotlin.api)
    api(libs.opentelemetry.kotlin.api.ext)

    testImplementation(libs.robolectric)
}
