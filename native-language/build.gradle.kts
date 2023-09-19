// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

gradlePlugin {
    plugins {
        create("native-application") {
            id = "br.dev.pedrolamarao.native.application"
            implementationClass = "br.dev.pedrolamarao.gradle.nativelanguage.NativeApplicationPlugin"
        }
        create("native-language") {
            id = "br.dev.pedrolamarao.native.language"
            implementationClass = "br.dev.pedrolamarao.gradle.nativelanguage.NativeLanguagePlugin"
        }
        create("native-archive") {
            id = "br.dev.pedrolamarao.native.archive"
            implementationClass = "br.dev.pedrolamarao.gradle.nativelanguage.NativeArchivePlugin"
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