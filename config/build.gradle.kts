plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
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
    publishing {
        singleVariant("release")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = project.findProperty("group")?.toString() ?: "com.github.uskhurshed"
                artifactId = "config"
                version = project.findProperty("version")?.toString() ?: project.version.toString()
            }
        }
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