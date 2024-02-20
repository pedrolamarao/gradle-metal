plugins {
    id("br.dev.pedrolamarao.metal.base")
}

dependencies {
    commands(project(":application"))
    commands(project(":base"))
    commands(project(":intermediate"))
    commands(project(":internal"))
}