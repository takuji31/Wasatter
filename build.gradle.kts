// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha03")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Deps.daggerVersion}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
