// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

gradlePlugin {
    plugins {
        create("application") {
            id = "br.dev.pedrolamarao.metal.application"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalApplicationPlugin"
        }
        create("archive") {
            id = "br.dev.pedrolamarao.metal.archive"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalArchivePlugin"
        }
        create("asm") {
            id = "br.dev.pedrolamarao.metal.asm"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.asm.MetalAsmPlugin"
        }
        create("base") {
            id = "br.dev.pedrolamarao.metal.base"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin"
        }
        create("c") {
            id = "br.dev.pedrolamarao.metal.c"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.c.MetalCPlugin"
        }
        create("commands") {
            id = "br.dev.pedrolamarao.metal.commands"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalRootPlugin"
        }
        create("cpp") {
            id = "br.dev.pedrolamarao.metal.cpp"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin"
        }
        create("cxx") {
            id = "br.dev.pedrolamarao.metal.cxx"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxPlugin"
        }
        create("prebuilt") {
            id = "br.dev.pedrolamarao.metal.prebuilt"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalPrebuiltPlugin"
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