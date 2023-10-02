pluginManagement {
    plugins {
        val metalVersion = "0.1+"
        id("br.dev.pedrolamarao.metal.application") version(metalVersion)
        id("br.dev.pedrolamarao.metal.cpp") version(metalVersion)
        id("br.dev.pedrolamarao.metal.cxx") version(metalVersion)
        id("br.dev.pedrolamarao.metal.prebuilt") version(metalVersion)
    }
    repositories {
        mavenLocal()
    }
}

rootProject.name = "sample"
include("application")
include("googletest")