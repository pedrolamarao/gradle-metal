plugins {
    id("br.dev.pedrolamarao.metal.base")
}

dependencies {
    commands(project(":application"))
    commands(project(":archive"))
}