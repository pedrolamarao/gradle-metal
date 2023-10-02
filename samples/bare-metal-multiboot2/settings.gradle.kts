pluginManagement {
    plugins {
        val metalVersion = "0.1+"
        id("br.dev.pedrolamarao.metal.application") version(metalVersion)
        id("br.dev.pedrolamarao.metal.archive") version(metalVersion)
        id("br.dev.pedrolamarao.metal.commands") version(metalVersion)
        id("br.dev.pedrolamarao.metal.cpp") version(metalVersion)
        id("br.dev.pedrolamarao.metal.cxx") version(metalVersion)
    }
    repositories {
        mavenLocal()
    }
}

rootProject.name = "sample"

include("application")
include("multiboot2")