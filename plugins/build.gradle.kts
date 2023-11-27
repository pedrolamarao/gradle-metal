// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("com.gradle.plugin-publish")
    id("jvm-test-suite")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val tagSet = setOf("bare-metal","asm","c","cpp","cxx","c++","native")

gradlePlugin {
    website = "https://github.com/pedrolamarao/gradle-metal"
    vcsUrl = "https://github.com/pedrolamarao/gradle-metal.git"
    plugins {
        create("application") {
            id = "br.dev.pedrolamarao.metal.application"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalApplicationPlugin"
            displayName = "Gradle Metal application plugin"
            description = "Configures a Gradle Metal application project"
            tags = tagSet
        }
        create("asm") {
            id = "br.dev.pedrolamarao.metal.asm"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalAsmPlugin"
            displayName = "Gradle Metal assembler language plugin"
            description = "Contributes assembler language support to a Gradle Metal project"
            tags = tagSet
        }
        create("base") {
            id = "br.dev.pedrolamarao.metal.base"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalBasePlugin"
            displayName = "Gradle Metal base plugin"
            description = "Contributes Gradle Metal services"
            tags = tagSet
        }
        create("c") {
            id = "br.dev.pedrolamarao.metal.c"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalCPlugin"
            displayName = "Gradle Metal C language plugin"
            description = "Contributes C language support to a Gradle Metal project"
            tags = tagSet
        }
        create("cxx") {
            id = "br.dev.pedrolamarao.metal.cxx"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalCxxPlugin"
            displayName = "Gradle Metal C++ language plugin"
            description = "Contributes C++ language support to a Gradle Metal project"
            tags = tagSet
        }
        create("library") {
            id = "br.dev.pedrolamarao.metal.library"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalLibraryPlugin"
            displayName = "Gradle Metal library plugin"
            description = "Configures a Gradle Metal library project"
            tags = tagSet
        }
        create("prebuilt") {
            id = "br.dev.pedrolamarao.metal.prebuilt"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.MetalPrebuiltPlugin"
            displayName = "Gradle Metal prebuilt plugin"
            description = "Configures a prebuilt Gradle Metal project"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
    }
}

testing {
    suites {
        register<JvmTestSuite>("functionalTest") {
            dependencies {
                implementation("org.assertj:assertj-core:3.24.2")
                implementation("org.junit.jupiter:junit-jupiter:5.9.0")
            }
        }
    }
}

gradlePlugin {
    testSourceSets(sourceSets["functionalTest"])
}

tasks.named("check") {
    dependsOn(testing.suites.named("functionalTest"))
}

tasks.named<Test>("functionalTest").configure {
    val metalPath = project.properties["metal.path"]
    if (metalPath != null) {
        systemProperty("metal.path",metalPath)
    }
}