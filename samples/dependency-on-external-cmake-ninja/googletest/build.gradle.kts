import org.ajoberstar.grgit.Grgit

plugins {
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.prebuilt")
    id("org.ajoberstar.grgit") version("5.2.0") apply(false)
}

val source = layout.projectDirectory.dir("src")
val build = layout.buildDirectory.dir("release").get()

val clone = tasks.register("clone") {
    outputs.dir(source)
    doLast {
        if (! source.dir(".git").asFile.exists()) {
            Grgit.clone {
                depth = 1
                dir = source
                uri = "https://github.com/google/googletest"
            }
        }
    }
}

val configure = tasks.register<Exec>("configure") {
    dependsOn(clone)
    inputs.dir(source)
    outputs.file(build.file("CMakeCache.txt"))
    commandLine("cmake","-B",build,"-DCMAKE_BUILD_TYPE=Release","-G","Ninja","-S",source)
}

val make = tasks.register<Exec>("make") {
    dependsOn(configure)
    commandLine("cmake","--build",build)
}

metal {
    prebuilt {
        includable(source.dir("googletest/include")) { builtBy(clone) }
        linkable(build.file("lib/gtest.lib")) { builtBy(make) }
    }
}