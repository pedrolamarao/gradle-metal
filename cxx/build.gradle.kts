// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

dependencies {
    implementation(project(":base"))
    implementation(project(":cpp"))
}

gradlePlugin {
    plugins {
        create("cxx-language") {
            id = "br.dev.pedrolamarao.metal.cxx"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.cxx.CxxPlugin"
        }
    }
}

testing {
    suites {
        register<JvmTestSuite>("functionalTest") {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.0")
            }
            useJUnitJupiter()
        }
    }
}

gradlePlugin {
    testSourceSets(sourceSets["functionalTest"])
}

tasks.check.configure {
    dependsOn(testing.suites.named("functionalTest"))
}