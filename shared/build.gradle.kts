plugins {
    id("com.android.library")
}

android {
    namespace = "com.psildave.punchtheclock.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.annotation)
}