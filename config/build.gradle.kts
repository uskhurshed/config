plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.easyapps.config"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {}
        debug {}
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation (libs.okhttp)
    implementation(libs.lottie)

}