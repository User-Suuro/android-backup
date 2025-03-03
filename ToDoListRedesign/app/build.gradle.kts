plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.todolist_redesign"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.todolist_redesign"
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
                srcDirs(
                    "src\\main\\res",
                    "src\\main\\res\\layouts\\auth",
                    "src\\main\\res\\layouts\\home",
                    "src\\main\\res\\drawables\\launcher",
                    "src\\main\\res\\drawables\\shapes",
                    "src\\main\\res\\drawables\\icons",
                )
            }
        }
    }
}

dependencies {
    // -- DEFAULT -- //
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // -- UTILS -- //
    implementation("com.google.code.gson:gson:2.8.9")
}