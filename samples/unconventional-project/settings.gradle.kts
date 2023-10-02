pluginManagement {
    plugins {
        val metalVersion = "0.1+"
        id("br.dev.pedrolamarao.metal.base") version(metalVersion)
    }
    repositories {
        mavenLocal()
    }
}

rootProject.name = "sample"