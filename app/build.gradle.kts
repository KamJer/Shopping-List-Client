plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "pl.kamjer.shoppinglist"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.kamjer.shoppinglist"
        minSdk = 26
        targetSdk = 35
        versionCode = 25
        versionName = "2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependenciesInfo {
        includeInApk = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.jackson)
    implementation(libs.material)
    implementation(libs.room.runtime)
    implementation(libs.room.rxjava3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.androidx.recyclerView)
    implementation(libs.zetetic.security)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    testCompileOnly(libs.lombok)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testAnnotationProcessor(libs.lombok)
}