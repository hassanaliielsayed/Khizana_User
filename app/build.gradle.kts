plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.1.10"

    id("com.google.dagger.hilt.android")
    kotlin("kapt")

    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.khizana_user"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.khizana_user"
        minSdk = 24
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
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation ("androidx.compose.material:material:1.5.4")

    //Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
//    //Room
//    val room_version = "2.6.1"
//    implementation("androidx.room:room-runtime:$room_version")
//    // Kotlin Symbol Processing (KSP)
//    ksp("androidx.room:room-compiler:$room_version")
//    // optional - Kotlin Extensions and Coroutines support for Room
//    implementation("androidx.room:room-ktx:$room_version")
    //Glide
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    //LiveData & Compose
    val compose_version = "1.0.0"
    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation("androidx.navigation:navigation-compose:2.9.0")


    // Location
    implementation(libs.play.services.location)

    // OSM Map
    implementation ("org.osmdroid:osmdroid-android:6.1.20")

    //Work Manager
//    implementation ("androidx.work:work-runtime-ktx:2.10.0")

    //MockK
    testImplementation ("io.mockk:mockk-android:1.13.17")
    testImplementation ("io.mockk:mockk-agent:1.13.17")
    implementation ("org.jetbrains.kotlin:kotlin-test:2.0.0")

    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.6.1")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")

    val koin_android_version = "4.0.2"
    implementation("io.insert-koin:koin-android:$koin_android_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_android_version")
    implementation("io.insert-koin:koin-androidx-compose-navigation:$koin_android_version")

    // Hilt Core
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

// Hilt + Jetpack Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    //firebase dependency
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")

    implementation ("com.google.accompanist:accompanist-pager:0.34.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.34.0")
    implementation ("io.coil-kt:coil-compose:2.4.0")

    //lottie
    implementation ("com.airbnb.android:lottie-compose:6.4.0")

    //serializable
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //data store
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    //google
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("com.google.firebase:firebase-auth-ktx")

    // google map
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.libraries.places:places:3.4.0")
    implementation ("com.google.maps.android:maps-compose:2.11.4")

    implementation ("com.google.accompanist:accompanist-permissions:0.34.0")

    implementation("androidx.webkit:webkit:1.14.0")

    implementation ("androidx.compose.foundation:foundation:1.5.0")

    //pager
    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.28.0")

}