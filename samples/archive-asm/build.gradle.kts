plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.asm")
}

tasks.wrapper.configure {
    gradleVersion = "8.4-rc-2"
}