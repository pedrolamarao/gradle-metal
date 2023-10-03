pluginManagement {
    repositories {
        mavenLocal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("br.dev.pedrolamarao.metal.")) {
                useModule("br.dev.pedrolamarao.gradle.metal:plugins:[0.1,0.2)")
            }
        }
    }
}

rootProject.name = "sample"

include("application")
include("multiboot2")