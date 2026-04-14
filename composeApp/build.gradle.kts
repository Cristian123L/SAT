import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    // 1. AGREGADO: Plugin de SQLDelight (versión estable 2.0.2)
    alias(libs.plugins.sqldelight)
}

// 2. AGREGADO: Configuración de SQLDelight
// Esto le dice a Gradle dónde generar las clases a partir del archivo .sq
sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.example.sat.db")
        }
    }
}

kotlin {
    androidTarget()

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            // 3. AGREGADO: Driver necesario para que SQLite funcione en Android
            implementation("app.cash.sqldelight:android-driver:2.0.2")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // Extensión de corrutinas para la base de datos
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")

            // implementation(libs.androidx.lifecycle.viewmodelCompose)
            // implementation(libs.androidx.lifecycle.runtimeCompose)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // 4. AGREGADO: Driver necesario para que SQLite funcione en Desktop (Windows/Mac/Linux)
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
        }
    }
}

android {
    namespace = "com.example.sat"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.sat"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.example.sat.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.sat"
            packageVersion = "1.0.0"
        }
    }
}