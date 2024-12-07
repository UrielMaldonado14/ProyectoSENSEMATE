plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sensematev2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sensematev2"
        minSdk = 33
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
}

dependencies {
    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    // Navigation Component (Fragment y UI)
    implementation("androidx.navigation:navigation-fragment:2.7.3")
    implementation("androidx.navigation:navigation-ui:2.7.3")

    // Material Design
    implementation("com.google.android.material:material:1.9.0")

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Core KTX
    implementation("androidx.core:core-ktx:1.12.0")

    // OSMDroid (Mapa)
    implementation("org.osmdroid:osmdroid-android:6.1.14")

    // OkHttp (Red)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // GridLayout (si lo utilizas en otros layouts)
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

configurations.all {
    resolutionStrategy {
        // Asegura la compatibilidad de Kotlin
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    }
}
