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

        resValue("string", "PB_URL", "https://suuro.pockethost.io");
        resValue("string", "PB_ADMIN_EMAIL", "godwingalvez26@gmail.com");
        resValue("string", "PB_PASSWORD", "anatadare123");
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
                srcDirs("src\\main\\res", "src\\main\\res\\layouts")

                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\auth")
                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\auth\\layout")

                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\home")
                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\home\\layout")

                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\products")
                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\products\\layout")

                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\stores")
                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\stores\\layout")

                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\profile")
                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\profile\\layout")

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


}