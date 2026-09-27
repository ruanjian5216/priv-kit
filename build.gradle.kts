import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

// vanniktech 发布插件只在显式发布（-Pprivkit.publish）时才加入 classpath。
// 日常构建以及被 skipad 通过 includeBuild 消费时都不解析该插件，
// 否则其 central-portal 组件会把 retrofit/okhttp/moshi 等与编译无关的
// 传递依赖拉到 buildscript classpath 并触发下载。
buildscript {
    val publishing = providers.gradleProperty("privkit.publish").isPresent
    dependencies {
        if (publishing) {
            // 版本与 gradle/libs.versions.toml 中的 maven-publish 保持一致。
            classpath("com.vanniktech:gradle-maven-publish-plugin:0.37.0")
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.remap) apply false
}

private object Cfg {
    const val compileSdk = 37
    const val buildToolsVersion = "37.0.0"
    const val ndkVersion = "30.0.14904198"
    const val cmakeVersion = "4.1.2"
    const val minSdk = 30
    val javaTargetVersion = JavaVersion.VERSION_11
    val kotlinJvmTarget = JvmTarget.fromTarget(javaTargetVersion.majorVersion)
    val kotlinLanguageVersion = KotlinVersion.KOTLIN_2_2
}

private val publishedModuleNames = setOf(
    "priv-shared",
    "priv-core",
    "priv-adb-crypto",
    "priv-ui",
)

allprojects {
    group = "io.github.priv-kit"
    version = "0.16.1" + if (rootProject.file("local.properties").isFile) "-SNAPSHOT" else ""
}

subprojects {
    // 发布相关配置（vanniktech 插件 apply 与 POM 配置）整体抽到独立脚本中，
    // 仅在带 -Pprivkit.publish 参数时加载；这样根构建脚本不再 import
    // vanniktech 的任何类型，日常构建的 classpath 上完全没有该插件。
    if (name in publishedModuleNames && providers.gradleProperty("privkit.publish").isPresent) {
        apply(from = "${rootProject.rootDir}/gradle/publish-conventions.gradle.kts")
    }

    fun configureExplicitApi() {
        if (name !in publishedModuleNames) {
            return
        }
        extensions.configure(KotlinBaseExtension::class.java) {
            explicitApi()
        }
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(Cfg.kotlinJvmTarget)
            languageVersion.set(Cfg.kotlinLanguageVersion)
            apiVersion.set(Cfg.kotlinLanguageVersion)
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        configureExplicitApi()

        extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = Cfg.javaTargetVersion
            targetCompatibility = Cfg.javaTargetVersion
        }
    }

    plugins.withType<AppPlugin> {
        configureExplicitApi()

        extensions.getByType(ApplicationExtension::class.java).apply {
            compileSdk = Cfg.compileSdk
            buildToolsVersion = Cfg.buildToolsVersion
            ndkVersion = Cfg.ndkVersion

            defaultConfig {
                minSdk = Cfg.minSdk
                targetSdk = Cfg.compileSdk
            }

            compileOptions {
                sourceCompatibility = Cfg.javaTargetVersion
                targetCompatibility = Cfg.javaTargetVersion
            }

            externalNativeBuild {
                cmake {
                    version = Cfg.cmakeVersion
                }
            }
        }
    }

    plugins.withType<LibraryPlugin> {
        configureExplicitApi()

        extensions.getByType(LibraryExtension::class.java).apply {
            compileSdk = Cfg.compileSdk
            buildToolsVersion = Cfg.buildToolsVersion
            ndkVersion = Cfg.ndkVersion

            defaultConfig {
                minSdk = Cfg.minSdk
            }

            compileOptions {
                sourceCompatibility = Cfg.javaTargetVersion
                targetCompatibility = Cfg.javaTargetVersion
            }

            externalNativeBuild {
                cmake {
                    version = Cfg.cmakeVersion
                }
            }
        }
    }
}
