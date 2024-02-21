plugins {
    id("br.dev.pedrolamarao.metal.base")
}

dependencies {
    commands("br.dev.pedrolamarao.gradle.metal.sample:application:1.0")
    commands("br.dev.pedrolamarao.gradle.metal.sample:archive:1.0")
}

tasks.build.configure {
    dependsOn( gradle.includedBuild("application").task(":build") )
}

tasks.clean.configure {
    gradle.includedBuilds.forEach { dependsOn(it.task(":clean")) }
}