plugins {
    id("otel.android-library-conventions")
    id("otel.publish-conventions")
}

description = "OpenTelemetry Android Instrumentation Auto Service"

android {
    namespace = "io.opentelemetry.android.instrumentation"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":services"))
    implementation(project(":session"))

    api(platform(libs.opentelemetry.platform.alpha))
    api(libs.opentelemetry.api)
    api(libs.compat.kotlin.to.official)
    api(libs.opentelemetry.kotlin.api)
    api(libs.opentelemetry.kotlin.api.ext)
    api(libs.opentelemetry.kotlin.api.noop)
}
