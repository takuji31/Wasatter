plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        versionCode(12)
        versionName("1.1.0a")
        applicationId("jp.senchan.android.wasatter")
        minSdkVersion(23)
        targetSdkVersion(30)
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
        getByName("androidTest").java.srcDir("src/androidTest/kotlin")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    composeOptions {
        kotlinCompilerVersion = Deps.kotlinVersion
        kotlinCompilerExtensionVersion = Deps.composeVersion
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")

    implementation("androidx.core:core-ktx:1.3.2")

    implementation("androidx.preference:preference-ktx:1.1.1")

    implementation("androidx.lifecycle:lifecycle-extensions:${Deps.AndroidX.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${Deps.AndroidX.lifecycleVersion}")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.android.material:material:1.2.1")

    implementation("androidx.compose.ui:ui:${Deps.composeVersion}")
    implementation("androidx.compose.ui:ui-tooling:${Deps.composeVersion}")
    implementation("androidx.compose.foundation:foundation:${Deps.composeVersion}")
    implementation("androidx.compose.material:material:${Deps.composeVersion}")
    implementation("androidx.compose.material:material-icons-core:${Deps.composeVersion}")
    implementation("androidx.compose.material:material-icons-extended:${Deps.composeVersion}")
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha04")

    androidTestImplementation("androidx.compose.ui:ui-test:${Deps.composeVersion}")

    implementation("com.google.dagger:hilt-android:${Deps.daggerVersion}")
    kapt("com.google.dagger:hilt-android-compiler:${Deps.daggerVersion}")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha02")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:${Deps.daggerVersion}")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${Deps.daggerVersion}")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:${Deps.daggerVersion}")
    kaptTest("com.google.dagger:hilt-android-compiler:${Deps.daggerVersion}")

    implementation("com.jakewharton.timber:timber:4.7.1")

    testImplementation("androidx.arch.core:core-testing:2.1.0")


    implementation("org.twitter4j:twitter4j-core:4.0.7")
    implementation("org.twitter4j:twitter4j-media-support:4.0.6")
    implementation("com.squareup.picasso:picasso:2.71828")
}

kapt {
    correctErrorTypes = true
}