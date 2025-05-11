plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "pl.kamjer.shoppinglist"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.kamjer.shoppinglist"
        minSdk = 26
        targetSdk = 34
        versionCode = 16
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
//            isMinifyEnabled  = true
//            isShrinkResources = true
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
    implementation(libs.material)
    implementation(libs.room.runtime)
    implementation(libs.room.rxjava3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.androidx.recyclerView)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    testCompileOnly(libs.lombok)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testAnnotationProcessor(libs.lombok)
}