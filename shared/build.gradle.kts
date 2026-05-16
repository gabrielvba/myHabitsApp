plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        // sqlite-driver is JVM-only; androidTest inherits from commonTest and adds it
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite)
            }
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
    }
}

android {
    namespace = "com.habitos.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}

sqldelight {
    databases {
        create("HabitosDatabase") {
            packageName.set("com.habitos.data.db")
        }
    }
}
