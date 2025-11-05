import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.scanner"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.scanner"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField(
            "String",
            "CLASH_ROYALE_API_KEY",
            "\"${properties.getProperty("CLASH_ROYALE_API_KEY")}\""
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Ktor Client, use to do query to an api
    implementation("io.ktor:ktor-client-core:3.3.1")
    implementation("io.ktor:ktor-client-android:3.3.1")

    // Ktor Serialization, use to read the json data
    implementation("io.ktor:ktor-client-content-negotiation:3.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.1")

    // Display an image from a url
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    //BDD
    implementation("io.github.pilgr:paperdb:2.7.2")

    //Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.3")

    // CameraX pour gérer la lecture de la camera
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ZXing pour décoder le QR code
    implementation("com.google.zxing:core:3.5.3")

    // (optionnel) Compose permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
}