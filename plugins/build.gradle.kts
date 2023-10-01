// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("com.gradle.plugin-publish")
    id("jvm-test-suite")
}

gradlePlugin {
    website = "https://github.com/pedrolamarao/gradle-metal"
    vcsUrl = "https://github.com/pedrolamarao/gradle-metal.git"
    plugins {
        create("application") {
            id = "br.dev.pedrolamarao.metal.application"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.application.MetalApplicationPlugin"
            displayName = "gradle-metal application component plugin"
            description = "Configures a conventional gradle-metal application component"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("archive") {
            id = "br.dev.pedrolamarao.metal.archive"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.archive.MetalArchivePlugin"
            displayName = "gradle-metal archive component plugin"
            description = "Configures a conventional gradle-metal archive component"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("asm") {
            id = "br.dev.pedrolamarao.metal.asm"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.asm.MetalAsmPlugin"
            displayName = "gradle-metal assembler language plugin"
            description = "Contributes assembler language support to gradle-metal components"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("base") {
            id = "br.dev.pedrolamarao.metal.base"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin"
            displayName = "gradle-metal base plugin"
            description = "Contributes native tasks and configurations"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("c") {
            id = "br.dev.pedrolamarao.metal.c"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.c.MetalCPlugin"
            displayName = "gradle-metal c language plugin"
            description = "Contributes C language support to gradle-metal components"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("commands") {
            id = "br.dev.pedrolamarao.metal.commands"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.commands.MetalCommandsPlugin"
            displayName = "gradle-metal commands database plugin"
            description = "Contributes a task to aggregate compile_commands.json database"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("cpp") {
            id = "br.dev.pedrolamarao.metal.cpp"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin"
            displayName = "gradle-metal cpp support plugin"
            description = "Contributes C preprocessor support to gradle-metal components"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("cxx") {
            id = "br.dev.pedrolamarao.metal.cxx"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxPlugin"
            displayName = "gradle-metal cxx language plugin"
            description = "Contributes C++ language support to gradle-metal components"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("prebuilt") {
            id = "br.dev.pedrolamarao.metal.prebuilt"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.prebuilt.MetalPrebuiltPlugin"
            displayName = "gradle-metal prebuilt plugin"
            description = "Permits defining a prebuilt gradle-metal component"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
        create("ixx") {
            id = "br.dev.pedrolamarao.metal.ixx"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.ixx.MetalIxxPlugin"
            displayName = "gradle-metal ixx plugin"
            description = "Contributes C++ module interface language support to gradle-metal components"
            tags = setOf("bare-metal","asm","c","cpp","cxx","c++","native")
        }
    }
}

testing {
    suites {
        register<JvmTestSuite>("functionalTest") {
            dependencies {
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