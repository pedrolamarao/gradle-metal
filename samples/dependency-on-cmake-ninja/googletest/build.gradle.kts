plugins {
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.prebuilt")
}

val source = layout.buildDirectory.dir("src").get()
val build = layout.buildDirectory.dir("obj").get()

val clone = tasks.register<Exec>("clone") {
    commandLine("git","clone","https://github.com/google/googletest",source)
    doFirst { delete(source) }
}

val configure = tasks.register<Exec>("configure") {
    dependsOn(clone)
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