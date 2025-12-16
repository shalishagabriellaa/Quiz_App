import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}


android {
    buildFeatures {
        compose = true
        buildConfig = true // Add this line
    }
    namespace = "com.example.tubes"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tubes"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val props = Properties()
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { props.load(it) }
        }

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            "\"${props.getProperty("GOOGLE_CLIENT_ID")}\""
        )
        buildConfigField("String", "CLOUDINARY_NAME",
        "\"${props.getProperty("cloudinary.name")}\""
        )

        buildConfigField("String", "CLOUDINARY_API_KEY",
        "\"${props.getProperty("cloudinary.apiKey")}\""
        )

        buildConfigField("String", "CLOUDINARY_API_SECRET",
        "\"${props.getProperty("cloudinary.apiSecret")}\""
        )
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))

    // Modul Compose (TANPA VERSI, biarkan BOM yang mengatur)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3) // Menggunakan Material 3
    implementation(libs.androidx.compose.foundation)
    implementation("androidx.compose.material:material-icons-extended") // Ikon M2/M3 hybrid, ini aman

    // --- INTI & LIFECYCLE ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose) // BOM akan mengatur versi ini
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // Tetapkan SATU versi stabil

    // --- FIREBASE BILL OF MATERIALS (BOM) ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.2")) // Versi stabil umum
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation(libs.firebase.crashlytics.buildtools)

    // --- NAVIGASI ---
    implementation("androidx.navigation:navigation-compose:2.7.7") // Tetapkan SATU versi stabil

    // --- DATABASE (ROOM) ---
    implementation(libs.androidx.room.ktx)
    // 'room-compiler-processing-testing' tidak diperlukan di sini, hanya untuk testing spesifik.

    // --- LIBRARY PIHAK KETIGA ---
    implementation("com.google.android.gms:play-services-auth:21.2.0") // Versi stabil umum
    implementation("com.cloudinary:cloudinary-android:2.3.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.patrykandpatrick.vico:compose:1.14.0")
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation(libs.androidx.material3)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation(libs.androidx.compose.runtime)
    implementation(libs.foundation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)



}