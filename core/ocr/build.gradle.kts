plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.dimasarya.billslice.core.ocr"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.mlkit.text.recognition)
    testImplementation(libs.junit)
}
