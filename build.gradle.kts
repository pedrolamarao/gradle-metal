// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("base")
}

group = "br.dev.pedrolamarao.gradle.metal"
version = "1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.wrapper.configure {
    gradleVersion = "8.3"
}