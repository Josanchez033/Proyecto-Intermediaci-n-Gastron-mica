import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    jacoco
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Datos de la firma release (llave .jks y contraseñas) en keystore.properties, en la raíz del proyecto.
val firma = Properties().apply {
    val archivo = rootProject.file("keystore.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "cl.dsy1105.cocinacapital"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "cl.dsy1105.cocinacapital"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Firma digital para publicar (IE 3.3.1): Build > Generate Signed Bundle / APK, o ./gradlew assembleRelease bundleRelease
    signingConfigs {
        create("release") {
            storeFile = rootProject.file(firma.getProperty("storeFile", "keystore/cocinacapital.jks"))
            storePassword = firma.getProperty("storePassword")
            keyAlias = firma.getProperty("keyAlias")
            keyPassword = firma.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            // Cobertura de las pruebas unitarias (Jacoco). Reporte: ./gradlew jacocoLogica
            enableUnitTestCoverage = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        // Las pruebas unitarias corren en la JVM: las clases de Android devuelven valores por defecto.
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.zxing.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

}

// ---------- Cobertura de la lógica (IE 3.2.1) ----------
// Mide solo el código lógico: datos, reglas, validaciones, ViewModels y llamadas a las APIs.
// Se excluye la interfaz (Composables), el tema, la navegación y el código que genera Room.
tasks.register<JacocoReport>("jacocoLogica") {
    group = "verification"
    description = "Corre las pruebas unitarias y genera el reporte de cobertura de la lógica"
    dependsOn("testDebugUnitTest")
    val clases = layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")
    classDirectories.setFrom(
        files(clases).asFileTree.matching {
            include(
                "cl/dsy1105/cocinacapital/data/**",
                "cl/dsy1105/cocinacapital/utils/**",
                "cl/dsy1105/cocinacapital/viewmodel/**"
            )
            exclude(
                "**/room/**",                                  // entidades y DAOs: Room genera su código
                "cl/dsy1105/cocinacapital/utils/FotoPerfil*",  // lee imágenes con APIs de Android
                "cl/dsy1105/cocinacapital/utils/Dispositivo*", // permisos, GPS y notificaciones del sistema
                "cl/dsy1105/cocinacapital/data/remote/RetrofitClient*", // solo arma los clientes HTTP
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    )
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }
}
