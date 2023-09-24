// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

gradlePlugin {
    plugins {
        create("application") {
            id = "br.dev.pedrolamarao.metal.application"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.NativeApplicationPlugin"
        }
        create("archive") {
            id = "br.dev.pedrolamarao.metal.archive"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.NativeArchivePlugin"
        }
        create("base") {
            id = "br.dev.pedrolamarao.metal.base"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin"
        }
        create("root") {
            id = "br.dev.pedrolamarao.metal.root"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.base.MetalRootPlugin"
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