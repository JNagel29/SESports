import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // Added for @Parcelize Annotation
}

android {
    namespace = "com.example.jetpacktest"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        compose     = true   // ← make sure compose is enabled
    }

    defaultConfig {
        applicationId = "com.example.jetpacktest"
        minSdk        = 26
        targetSdk     = 34
        versionCode   = 1
        versionName   = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // For hiding keys in local properties
        val localPropertiesFile = File("local.properties")
        val properties        = Properties()
        localPropertiesFile.inputStream().use { properties.load(it) }
        buildConfigField("String", "BDL_KEY", "\"${properties.getProperty("BDL_KEY")}\"")
        buildConfigField("String", "SPORTS_DATA_IO_KEY", "\"${properties.getProperty("SPORTS_DATA_IO_KEY")}\"")
        buildConfigField("String", "DB_USER", "\"${properties.getProperty("DB_USER")}\"")
        buildConfigField("String", "DB_PASS", "\"${properties.getProperty("DB_PASS")}\"")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
    // your existing deps
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(files("libs/mysql-connector-java-5.1.44-bin.jar"))
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.4")
    implementation("com.google.android.gms:play-services-dtdi:16.0.0-beta01")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation("com.github.Rafsanjani:datepickertimeline:0.7.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    // ↓ downgraded to compile with SDK 34:
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.13")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.1.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    // ––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––

    // ← added for props feature:
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
