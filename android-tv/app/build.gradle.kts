// Build settings for the launcher app.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sahaj.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sahaj.launcher"
        minSdk = 21          // Android 5.0 - covers old/low-end Android TVs
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            // Shrinks the APK and makes it faster. It is signed with the standard
            // debug key so you can install it directly (no Play Store needed).
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

// No dependencies on purpose (plain Android framework only) - keeps the app light.
dependencies {
}
