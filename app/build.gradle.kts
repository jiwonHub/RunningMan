@file:Suppress("UNUSED_EXPRESSION")

import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id ("com.google.gms.google-services")
    id ("com.google.android.gms.oss-licenses-plugin")
}

val properties = Properties()
val localProperties = rootProject.file("local.properties")
if (localProperties.exists()) {
    properties.load(FileInputStream(localProperties))
}

android {
    namespace = "com.cjwjsw.runningman"
    compileSdk = 34
    // 필요 시 추가
    // 필요 시 추가
    packaging {
        resources {
            excludes += ("META-INF/NOTICE.md")
        }
    }


    defaultConfig {
        applicationId = "com.cjwjsw.runningman"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField( "String", "ReportAppKey", properties["ReportAppKey"].toString())
        buildConfigField( "String", "ReportMail", properties["ReportMail"].toString())
        manifestPlaceholders["naver_map_api_key"] = properties["naver_map_api_key"].toString()
        buildConfigField( "String", "default_web_client_id", properties["default_web_client_id"].toString())
        buildConfigField( "String", "TestMail", properties["TestMail"].toString())

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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.firebase.database.ktx)
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))


    val room_version = "2.6.1"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constraintlayout)

    //카카오 로그인 API
    implementation ("com.kakao.sdk:v2-user:2.20.1")

    implementation ("com.google.android.material:material:1.9.0")

    implementation ("com.google.dagger:hilt-android:2.51.1")
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
    implementation ("androidx.hilt:hilt-work:1.0.0")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation ("com.google.firebase:firebase-firestore:25.0.0")

    implementation ("androidx.fragment:fragment-ktx:1.7.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation("androidx.activity:activity-ktx:1.9.0")

    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    kapt ("com.github.bumptech.glide:compiler:4.14.2")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation ("com.google.android.gms:play-services-oss-licenses:17.1.0")

    implementation("me.relex:circleindicator:1.3.2")
    implementation("me.relex:circleindicator:2.1.6")

    implementation("com.naver.maps:map-sdk:3.18.0")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")

}

kapt {
    correctErrorTypes = true
}