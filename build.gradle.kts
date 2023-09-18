plugins {
    id("base")
}

group = "br.dev.pedrolamarao.gradle.native"
version = "1.0-SNAPSHOT"

tasks.wrapper.configure {
    gradleVersion = "8.3"
}