// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    plugins {
        id("com.gradle.plugin-publish") version("1.2.1")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "gradle-metal"

include("plugins")