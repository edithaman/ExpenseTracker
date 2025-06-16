// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    //id("com.google.gms.google-services") apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
    //id("com.google.dagger.hilt.android")version "2.56.2"
}
buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.2") // Use latest
        classpath("com.google.gms:google-services:4.4.2")
    }
}