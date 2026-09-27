// priv-kit 发布约定脚本。
//
// 该脚本只在显式发布（根构建脚本检测到 -Pprivkit.publish）时通过
// apply(from = "gradle/publish-conventions.gradle.kts") 加载。
// 注意：不要在根 build.gradle.kts 中 import 或引用 vanniktech 的任何类型，
// 否则即使不执行发布，Gradle 也必须先解析整个发布插件 classpath
// （retrofit / okhttp / moshi 等传递依赖都会被下载）。
import com.vanniktech.maven.publish.MavenPublishBaseExtension

pluginManager.apply("com.vanniktech.maven.publish")

extensions.configure(MavenPublishBaseExtension::class.java) {
    coordinates(project.group.toString(), project.name, project.version.toString())
    if (project.providers.gradleProperty("signing.keyId").isPresent) {
        publishToMavenCentral()
        signAllPublications()
    }

    val repoUrl = "https://github.com/priv-kit/priv-kit"
    pom {
        name.set("Priv Kit")
        description.set("Self-managed privileged runtime for Android apps.")
        url.set(repoUrl)
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                name.set("lisonge")
                email.set("i@songe.li")
                url.set("https://github.com/lisonge")
            }
        }
        scm {
            url.set(repoUrl)
        }
    }
}
