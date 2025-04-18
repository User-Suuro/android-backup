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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        getByName("main") {
            res {
                srcDirs (
                        "src\\main\\res",
                        "src\\main\\assets",
                        "src\\main\\res\\layouts",

                        "src\\main\\res\\layouts\\auth",

                        "src\\main\\res\\layouts\\home",
                        "src\\main\\res\\layouts\\home\\products",
                        "src\\main\\res\\layouts\\home\\create_join",

                        "src\\main\\res\\layouts\\more",
                        "src\\main\\res\\layouts\\discover",

                        "src\\main\\res\\layouts\\components",
                        "src\\main\\res\\layouts\\components\\dialog",
                        "src\\main\\res\\layouts\\components\\item",

                        "src\\main\\res\\drawables",
                        "src\\main\\res\\drawables\\icons",
                )
            }
        }
    }


}

dependencies {
    // Default
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // JSON Parser
    implementation("com.google.code.gson:gson:2.10.1")

    // Http
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")

    // Image Loader
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Icon animation
    implementation("com.airbnb.android:lottie:6.6.6")
    
    // Barcode
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    //env
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
}