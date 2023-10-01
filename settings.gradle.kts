// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

pluginManagement {
    plugins {
        id("com.gradle.plugin-publish") version("1.2.1")
        id("com.palantir.git-version") version("3.0.0")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "gradle-metal"

include("plugins")