plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    implementation(project(":multiboot2"))
}

metal {
    applications {
        named("main") {
            val linkerFile = file("../i686-elf.ld")
            linkOptions = listOf("-nostdlib","-static","-Wl,--script=${linkerFile}")
        }
    }
}