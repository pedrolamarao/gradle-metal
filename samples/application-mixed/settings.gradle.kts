pluginManagement {
    plugins {
        val metalVersion = "0.1+"
        id("br.dev.pedrolamarao.metal.application") version(metalVersion)
        id("br.dev.pedrolamarao.metal.asm") version(metalVersion)
        id("br.dev.pedrolamarao.metal.c") version(metalVersion)
        id("br.dev.pedrolamarao.metal.cxx") version(metalVersion)
    }
    repositories {
        mavenLocal()
    }
}

rootProject.name = "sample"