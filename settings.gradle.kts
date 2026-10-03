pluginManagement {
    repositories {
        // 腾讯云 Maven 公共代理，优先用于 Gradle 插件解析（国内网络直达）。
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        google()
    }
}

// hyper-ui 本地源码库路径：当前项目与 hyper-ui 仓库同级，真正的 Android Library 位于 hyper-ui/library。
// 通过 composite build 引入，重构后的 priv-ui 直接编译本地 hyper-ui 源码，无需先发布到远端仓库。
val hyperUiLocalBuild = file("../hyper-ui/library").normalize()
check(hyperUiLocalBuild.isDirectory) {
    "本地 hyper-ui 库不存在：${hyperUiLocalBuild.absolutePath}。请确认 hyper-ui 仓库路径。"
}

includeBuild(hyperUiLocalBuild) {
    dependencySubstitution {
        substitute(module("com.hyperui:hyper-ui")).using(project(":"))
    }
}

rootProject.name = "priv-kit"

// 已去除 Compose Multiplatform：:priv-playground 是 Kotlin/Wasm + Desktop 演示，
// 依赖 priv-ui 的多平台目标，重构为纯 Android 后不再参与构建。
include(":priv-ui")

if (!providers.environmentVariable("PRIV_KIT_SKIP_ANDROID").isPresent) {
    include(
        ":priv-shared",
        ":priv-core",
        ":priv-adb-crypto",
        ":hidden-api",
    )
    // priv-sample 是独立演示 App，仅 priv-kit 仓库自身开发时需要；
    // 被其他工程以 includeBuild 复合构建引入时（如 skipad），消费方可通过
    // JVM 系统属性 -DprivKit.includeSample=false（写入其 gradle.properties 的
    // org.gradle.jvmargs 即可对命令行和 Android Studio 同时生效）将其排除，
    // 避免它作为 application 模块出现在消费方 Android Studio 的打包向导中导致误选。
    // 注意：根工程 gradle.properties 里的普通 -P project 属性不会传递给
    // included build 的 settings 脚本，因此这里以 systemProperty 为准。
    val includeSample = providers
        .systemProperty("privKit.includeSample")
        .orElse(providers.gradleProperty("privKit.includeSample"))
        .orElse("true")
        .get()
        .toBoolean()
    if (includeSample) {
        include(":priv-sample")
    }
}
