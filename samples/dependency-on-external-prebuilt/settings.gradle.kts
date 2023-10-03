pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("br.dev.pedrolamarao.metal.")) {
                useModule("br.dev.pedrolamarao.gradle.metal:plugins:0.1-rc-0")
            }
        }
    }
}

include("application")
include("googletest")