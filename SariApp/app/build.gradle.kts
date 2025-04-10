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
    sourceSets {
        getByName("main") {
            res {
                srcDirs (
                        "src\\main\\res",
                        "src\\main\\res\\layouts",
                        "src\\main\\res\\layouts\\auth",
                        "src\\main\\res\\layouts\\auth\\layout",
                        "src\\main\\res\\layouts\\home",
                        "src\\main\\res\\layouts\\home\\layout",
                        "src\\main\\res\\layouts\\products",
                        "src\\main\\res\\layouts\\products\\layout",
                        "src\\main\\res\\layouts\\stores",
                        "src\\main\\res\\layouts\\stores\\layout",
                        "src\\main\\res\\layouts\\profile",
                        "src\\main\\res\\layouts\\profile\\layout",
                        "src\\main\\res\\drawables",
                        "src\\main\\res\\drawables\\icons",
                        "src\\main\\res\\layouts\\components",
                        "src\\main\\res\\layouts\\components\\dialog"
                )
            }
        }
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

    // JSON Parser
    implementation("com.google.code.gson:gson:2.8.9")

    // Http
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")

    // Image Loader
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Barcode
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}