plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Добавляем плагин для автоматической генерации Parcelable
    id("kotlin-parcelize")
}

android {
    namespace = "com.artspb.playlistmaker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.artspb.playlistmaker"
        minSdk = 29
        targetSdk = 36
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Я подключаю библиотеку Glide для удобной загрузки,
    // кэширования и трансформации изображений (обложек)
    implementation(libs.glide)
    // Подключаем Retrofit для сетевых запросов и конвертер Gson для парсинга JSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.koin.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}