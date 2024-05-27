plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.cjwjsw.runningman"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cjwjsw.runningman"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constraintlayout)

    //카카오 로그인 API
    implementation ("com.kakao.sdk:v2-user:2.20.1")

    implementation ("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    implementation ("com.google.firebase:firebase-firestore:25.0.0")

    implementation ("androidx.fragment:fragment-ktx:1.7.1")
    implementation("androidx.activity:activity-ktx:1.2.3")
}

kapt {
    correctErrorTypes = true
}