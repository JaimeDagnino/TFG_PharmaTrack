plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pharmatrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pharmatrack"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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

    // ----------------------------
    // BLOQUE PARA EVITAR DUPLICADOS DE RECURSOS
    // (Arthur: modifica según veas qué recurso concreto sale duplicado)
    // ----------------------------
    packaging {
        resources {
            // Normalmente el .aar de MaterialCalendarView trae varios archivos LICENSE/NOTICE
            // que pueden ya existir en otras dependencias. Los excluimos todos:
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE"

            // Si el error de duplicado menciona otro archivo (p.ej. un layout en res/…),
            // añádelo aquí como `pickFirst("res/layout/nombre_layout.xml")`
            // o `excludes += "res/layout/nombre_layout.xml"`.
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.threetenabp)
    implementation (libs.lottie)


    // ----------------------------------------
    // MaterialCalendarView 2.x (JitPack)
    // Excluimos TODO lo que venga de `com.android.support`
    // para evitar que permanezcan clases antiguas de soporte
    // ----------------------------------------
    implementation(libs.material.calendarview) {
        // Esto excluye cualquier artefacto que venga del group "com.android.support"
        // (support-compat, support-v4, etc). De esta forma no quedará ninguna clase duplicada.
        exclude(group = "com.android.support")
        // Además, ya dejábamos fuera appcompat de AndroidX (por si venía alguna versión repetida).
        exclude(group = "androidx.appcompat", module = "appcompat")
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

