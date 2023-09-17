plugins {
    id("java-gradle-plugin")
    id("jvm-test-suite")
}

dependencies {
    implementation(project(":cxx-language"))
}

gradlePlugin {
    plugins {
        create("cxx-application") {
            id = "br.dev.pedrolamarao.cxx.application"
            implementationClass = "br.dev.pedrolamarao.gradle.cxx.application.CxxApplicationPlugin"
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
        register<JvmTestSuite>("integrationTest") {

        }
    }
}

gradlePlugin {
    testSourceSets(sourceSets["functionalTest"])
}