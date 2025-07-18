// Module : app
plugins {
    alias(libs.plugins.android.application)  //
    alias(libs.plugins.kotlin.android)       //
    alias(libs.plugins.kotlin.compose)       //
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.kapt")       // Unresolved reference 'kotlin_kapt'.
}


android {
    namespace = "com.weblite.kgf"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.weblite.kgf"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //icons
    implementation("androidx.compose.material:material-icons-extended")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation("androidx.compose.foundation:foundation:1.6.0")
    //coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.navigation:navigation-compose:2.7.5" )

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    // Dependency Injection (Hilt)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48") //Unresolved reference 'kapt'.
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Networking (Retrofit)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Image Loading (Coil)
    implementation ("io.coil-kt:coil-compose:2.4.0")

    // Logging
    implementation ("com.jakewharton.timber:timber:5.0.1")
    implementation ("androidx.compose.material:material:1.4.0")
    implementation("androidx.compose.material:material:1.5.4")

    implementation ("androidx.compose.material3:material3:1.2.1")


}