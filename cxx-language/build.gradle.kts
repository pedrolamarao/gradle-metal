// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

dependencies {
    implementation(project(":base"))
}

gradlePlugin {
    plugins {
        create("cxx-language") {
            id = "br.dev.pedrolamarao.cxx.language"
            implementationClass = "br.dev.pedrolamarao.gradle.cxx.language.CxxLanguagePlugin"
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