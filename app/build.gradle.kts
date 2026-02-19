plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "pl.kamjer.shoppinglist"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

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
                "proguard-rules.pro",
            )
            buildConfigField("String", "RECIPE_URL", "\"recipe.kamjer.online\"")
            buildConfigField("String", "SHOPPING_URL", "\"shoppinglist.kamjer.online\"")
            buildConfigField("String", "USER_URL", "\"user.kamjer.online\"")
            buildConfigField("String", "WEBSOCKET_BASE_URL", "\"wss://\"")
            buildConfigField("String", "HTTP_BASE_URL", "\"https://\"")
        }
        debug {
            buildConfigField("String", "SHOPPING_URL", "\"192.168.0.13:5443\"")
            buildConfigField("String", "RECIPE_URL", "\"192.168.0.13:6443\"")
            buildConfigField("String", "USER_URL", "\"192.168.0.13:4443\"")
            buildConfigField("String", "WEBSOCKET_BASE_URL", "\"ws://\"")
            buildConfigField("String", "HTTP_BASE_URL", "\"http://\"")
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
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.androidx.recyclerView)
    implementation(libs.zetetic.security)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.guava)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    testCompileOnly(libs.lombok)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testAnnotationProcessor(libs.lombok)
}