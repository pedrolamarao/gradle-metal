// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("base")
}

group = "br.dev.pedrolamarao.gradle.metal"
version = "0.1-rc-1"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.wrapper.configure {
    gradleVersion = "8.4-rc-3"
}