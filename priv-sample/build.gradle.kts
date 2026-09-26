import com.android.build.api.variant.ResValue

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "priv.kit.sample"

    defaultConfig {
        applicationId = "priv.kit.sample"
        versionCode = 1
        versionName = project.version.toString()
        // 重构后 priv-ui 依赖 hyper-ui（minSdk 30），应用整体 minSdk 随之上调。
        minSdk = 30
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
        }
    }

    buildFeatures {
        aidl = true
        compose = true
        resValues = true
    }
}

androidComponents.onVariants { variant ->
    val baseAppLabel = "Priv"
    val appLabel = if (variant.buildType == "debug") "$baseAppLabel-Dev" else baseAppLabel
    variant.resValues.put(
        variant.makeResValueKey("string", "app_name"),
        ResValue(appLabel),
    )
}

dependencies {
    compileOnly(project(":hidden-api"))
    implementation(project(":priv-core"))
    implementation(project(":priv-ui"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.hyper.ui)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.hiddenapibypass)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
}
