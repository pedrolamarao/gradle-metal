// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("com.palantir.git-version")
    id("base")
}

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

group = "br.dev.pedrolamarao.gradle.metal"
version = versionDetails().let { "0.1+${it.gitHash}" }

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.wrapper.configure {
    gradleVersion = "8.4-rc-3"
}