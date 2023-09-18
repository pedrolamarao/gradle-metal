// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

gradlePlugin {
    plugins {
        create("asm-language") {
            id = "br.dev.pedrolamarao.asm.language"
            implementationClass = "br.dev.pedrolamarao.gradle.asm.language.AsmLanguagePlugin"
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