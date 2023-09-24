plugins {
    id("br.dev.pedrolamarao.metal.root")
}

dependencies {
    commands(project(":bar"))
    commands(project(":foo"))
    commands(project(":googletest"))
    commands(project(":meh"))
}

tasks.wrapper.configure {
    gradleVersion = "8.3"
}