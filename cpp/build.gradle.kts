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
        create("cpp") {
            id = "br.dev.pedrolamarao.metal.cpp"
            implementationClass = "br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin"
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