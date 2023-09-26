plugins {
    id("br.dev.pedrolamarao.metal.commands")
}

dependencies {
    commands(project(":bar"))
    commands(project(":foo"))
    commands(project(":googletest"))
    commands(project(":meh"))
}

tasks.register("precompile")

tasks.register("clion") {
    dependsOn("precompile","commands")
}

tasks.wrapper.configure {
    gradleVersion = "8.3"
}