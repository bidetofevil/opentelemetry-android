plugins {
    id("otel.android-library-conventions")
    id("otel.publish-conventions")
}

description = "OpenTelemetry Android activity instrumentation"

android {
    namespace = "io.opentelemetry.android.instrumentation.activity"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    api(platform(libs.opentelemetry.platform.alpha))
    api(libs.opentelemetry.api)
    api(libs.opentelemetry.kotlin)
    api(libs.opentelemetry.kotlin.compat)
    api(project(":instrumentation:common-api"))
    api(project(":instrumentation:android-instrumentation"))
    implementation(project(":services"))
    implementation(project(":session"))
    implementation(project(":common"))
    implementation(libs.opentelemetry.sdk)
    implementation(libs.androidx.core)
    implementation(libs.opentelemetry.instrumentation.api)
    testImplementation(libs.robolectric)
    testImplementation(libs.opentelemetry.kotlin.testing)
}
