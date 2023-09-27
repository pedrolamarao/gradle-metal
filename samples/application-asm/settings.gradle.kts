pluginManagement {
    plugins {
        id("br.dev.pedrolamarao.metal.application") version("1.0-SNAPSHOT")
        id("br.dev.pedrolamarao.metal.asm") version("1.0-SNAPSHOT")
    }
    repositories {
        mavenLocal()
    }
}

rootProject.name = "sample"