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
        ":priv-sample",
        ":hidden-api",
    )
}
