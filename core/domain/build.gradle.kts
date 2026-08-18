plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":core:model"))
    testImplementation(libs.junit)
    testImplementation(project(":core:testing"))
}
