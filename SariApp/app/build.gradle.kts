import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
val APP_WRITE_ID = gradleLocalProperties(rootDir).getProperty("APP_WRITE_ID", "");

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.sariapp"
    compileSdk = 34

    defaultConfig {
 applicationId = "com.example.sariapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue ("string", "APP_WRITE_ID", "\"" + APP_WRITE_ID + "\"")
        resValue ("string", "APP_WRITE_CALLBACK", "\"appwrite-callback-${APP_WRITE_ID}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}

dependencies {
    // Default
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Prefs
    implementation("com.google.code.gson:gson:2.8.9")

    // Http
    implementation ("com.squareup.retrofit2:retrofit:2.9.0") // For API calls
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")  // JSON parsing
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")  // Optional for logging

    // Image Loader
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")


}