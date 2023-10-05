plugins {
    id("br.dev.pedrolamarao.metal.commands")
}

dependencies {
    commands(project(":application"))
    commands(project(":multiboot2"))
}

val grubMakeStandalone = tasks.register<Exec>("grub-make-standalone") {
    dependsOn(":application:link")
    val configurationFile = layout.projectDirectory.file("grub.cfg")
    val toolPath = if (properties.contains("grub.path")) "${properties["grub.path"]}/grub-mkstandalone" else "grub-mkstandalone"
    val inputFile = layout.projectDirectory.file("application/build/exe/main/application.exe")
    val outputFile = layout.buildDirectory.file("image")
    executable(toolPath)
    args(
        "--format=i386-pc",
        "--output=\"${outputFile.get()}\"",
        "--themes=",
        "--fonts=",
        "--locales=",
        "--install-modules=configfile memdisk multiboot2 normal",
        "\"/boot/grub/grub.cfg=${configurationFile}\"",
        "\"/program=${inputFile}\""
    )
    doFirst {
        mkdir(layout.buildDirectory)
    }
}

tasks.assemble.configure {
    dependsOn(grubMakeStandalone)
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}