plugins {
    id("br.dev.pedrolamarao.metal.base")
}

tasks.build.configure {
    dependsOn( gradle.includedBuild("application").task(":build") )
}

tasks.clean.configure {
    gradle.includedBuilds.forEach { dependsOn(it.task(":clean")) }
}